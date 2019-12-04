const fs = require('fs');

const memory = new WebAssembly.Memory({ initial: 1 })

function logString(offset, length) {
  var bytes = new Uint8Array(memory.buffer, offset, length);
  const string = new TextDecoder('utf8').decode(bytes);
  console.log(string);
}

function logFloat(int) { // assumes an f32, max.
  console.log(int);
}

var importObject = { console: { logFloat, logString }, js: { mem: memory } };

const path = process.argv[2]
WebAssembly.instantiate(fs.readFileSync(path), importObject)
  .then(obj => obj.instance.exports.main())
  .catch(error => console.log(error));
