const fs = require('fs');

function consoleLogString(offset, length, type) {
  var bytes = new Uint8Array(memory.buffer, offset, length);
  switch (type) {
    case type === 0: // int
      let value = 0;
      for (let i = byteArray.length - 1; i >= 0; i -= 1) {
        value = (value * 256) + byteArray[i];
      }
      console.log(value)
      break;
    case type === 1: // string
      const string = new TextDecoder('utf8').decode(bytes);
      console.log(string);
      break;
    default:
      throw new Error(`Runtime error: unrecognized type ${type}`)
  }
}

var memory = new WebAssembly.Memory({initial:1});

var importObject = { console: { log: consoleLogString }, js: { mem: memory } };

const path = process.argv[2]
WebAssembly.instantiate(fs.readFileSync(path), importObject)
  .then(obj => {
    obj.instance.exports.main();
    console.log(memory.buffer);
  });
