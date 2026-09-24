const fs = require('node:fs');
const path = require('node:path');
const webFrontend = path.resolve(__dirname, '..', '..', 'web-app', 'frontend', 'index.html');
const bundledFrontend = path.resolve(__dirname, '..', 'www', 'index.html');
const source = fs.existsSync(webFrontend) ? webFrontend : bundledFrontend;
const destination = path.resolve(__dirname, '..', 'www', 'index.html');
fs.mkdirSync(path.dirname(destination), { recursive: true });
if (source !== destination) fs.copyFileSync(source, destination);
console.log(`Using ${source === webFrontend ? 'web-app/frontend/index.html' : 'mobile/www/index.html'} for the Android build.`);
