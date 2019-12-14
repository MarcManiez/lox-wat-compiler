# WAT compiler

[Web assembly text format](https://webassembly.org/docs/text-format/) is an easier way for humans to write web assembly than raw binary instructions.

This repo contains two things:
- a Lox interpeter, created by reading along the excellent [@munificent/craftinginterpreters](https://github.com/munificent/craftinginterpreters).
- a small WAT compiler for a subset of the features of Lox.

This is the first compiler I write, and WAT seemed appropriately challenging, but also convenient because the output is easier to grasp. Working from an implementation of Lox that I was familiar with was a nice boost too!

With maven and Java 10 installed, you can fool around with print statements, additions, strings and numbers inside of [test.lox](./test.lox).

To compile the contents, and see the output of the wat file in your console, use the command [`./runLoxAsWasm`](./runLoxAsWasm).
