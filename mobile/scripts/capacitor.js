const os = require('node:os');
const userInfo = os.userInfo.bind(os);
os.userInfo = (...args) => {
  try {
    return userInfo(...args);
  } catch {
    return {
      uid: -1,
      gid: -1,
      username: process.env.USERNAME || 'android-builder',
      homedir: process.env.USERPROFILE || process.cwd(),
      shell: null,
    };
  }
};
require('@capacitor/cli/bin/capacitor');
