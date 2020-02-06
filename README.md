# WAT compiler

[Web assembly text format](https://webassembly.org/docs/text-format/) is an easier way for humans to write web assembly than raw binary instructions.

This repo contains two things:
- a Lox interpeter, created by reading along the excellent [@munificent/craftinginterpreters](https://github.com/munificent/craftinginterpreters)
- a small WAT compiler for a subset of the features of Lox

This is the first compiler I've writen, and WAT seemed appropriately challenging, but also convenient because the output is easier to grasp. Working from an implementation of Lox that I was familiar with was a nice boost too!

## Playing with lox

- Install Maven
- You can then play with lox in an REPL using [`./jlox`](./jlox) from the root folder
- To run lox from a file, run `./jlox [filename] --compile`

## Compiling to WAT

To compile the contents, and see the output of the wat file in your console:
- Install the [Web Assembly Binary Toolkit](https://github.com/WebAssembly/wabt), following these [instructions](https://github.com/WebAssembly/wabt#cloning)
- Add the newly created build folder to your path for convenience (and for my scripts to work!)
- Write some lox inside of [test.lox](./test.lox). My compiler only supports a subset of the language: strings, the `+` operator, numbers, and print statements!
- use the command [`./runLoxAsWasm`](./runLoxAsWasm) from this project's root folder
- view the output in `output.wat`, and in the console (if you've decided to print anything)
