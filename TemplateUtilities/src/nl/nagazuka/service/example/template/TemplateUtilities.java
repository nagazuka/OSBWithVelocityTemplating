package nl.nagazuka.service.example.template;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;

public class TemplateUtilities {

	private static final String VELOCITY_PROPERTIES = "velocity.properties";

	/**
	 * @param renderTemplateReq
	 * @return
	 */
	public static String renderTemplate(XmlObject renderTemplateReq) {
		String templateName = getTemplateName(renderTemplateReq);
		Map<String, String> params = getParameters(renderTemplateReq);

		/* Initialize Velocity singleton */
		Properties properties = null;

		try {
			URL url = ClassLoader.getSystemResource(VELOCITY_PROPERTIES);
			if (url != null) {
				properties = new Properties();
				properties.load(url.openStream());
			}
		} catch (IOException e1) {
			throw new IllegalArgumentException(
					"Could not load velocity.properties from classpath due to IO exception.");
		}

		if (properties == null) {
			throw new IllegalArgumentException(
					"Failed to load velocity.properties from classpath. Check whether property file named ["
							+ VELOCITY_PROPERTIES
							+ "] is present on classpath.");
		}

		Velocity.init(properties);

		VelocityContext context = new VelocityContext();

		for (String key : params.keySet()) {
			String value = params.get(key);
			if (value != null && value.length() > 0) {
				context.put(key, value);
			}
		}

		/* lets render a template */
		StringWriter w = new StringWriter();

		try {
			Velocity.mergeTemplate(templateName, "UTF-8", context, w);
		} catch (ResourceNotFoundException e) {
			throw new IllegalArgumentException("Could not find template ["
					+ templateName + "] in directory ["
					+ properties.getProperty("file.resource.loader.path")
					+ "].");
		}

		return w.toString();
	}

	private static Map<String, String> getParameters(XmlObject renderTemplateReq) {
		Map<String, String> res = new HashMap<String, String>();
		XmlObject[] parameters = renderTemplateReq
				.selectPath("./renderTemplate/parameters/parameter");
		for (XmlObject parameter : parameters) {
			String key = getChildValue(parameter, "./key");
			String value = getChildValue(parameter, "./value");
			res.put(key, value);
		}
		return res;
	}

	private static String getTemplateName(XmlObject renderTemplateReq) {
		return getChildValue(renderTemplateReq, "./renderTemplate/templateName");
	}

	private static String getChildValue(XmlObject root, String xpath) {
		XmlObject child = root.selectPath(xpath)[0];
		XmlCursor cursor = child.newCursor();
		return cursor.getTextValue();
	}
	
}
