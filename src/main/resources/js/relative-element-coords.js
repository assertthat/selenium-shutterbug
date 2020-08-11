var rect = arguments[0].getBoundingClientRect();
return ['' + parseInt(rect.left), '' + parseInt(rect.top), '' + parseInt(rect
.width), '' + parseInt(rect.height), '' +  parseInt(arguments[0].scrollWidth),
'' + parseInt(arguments[0].scrollHeight)
];