
declare namespace xf = "http://tempuri.org/TemplateService/TransformResponse/";

declare function xf:TransformResponse($string1 as xs:string)
    as xs:string {
        $string1
};

declare variable $string1 as xs:string external;

xf:TransformResponse($string1)
