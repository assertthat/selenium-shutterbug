({
    width: Math.max(window.innerWidth, document.body.scrollWidth, document.documentElement.scrollWidth) | 0,
    height: Math.max(window.innerHeight, document.body.scrollHeight, document.documentElement.scrollHeight) | 0,
    deviceScaleFactor: window.devicePixelRatio || 1, mobile: typeof window.orientation !== 'undefined'
});
