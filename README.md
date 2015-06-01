# parsop
An Operator Grammar Parser Generator, written in Java.

## Goal
The goal of this project is to write a deterministic and simple parser generator
for Operator Grammars (Grammars in which *identifiers* are combined by *operators*).
The operators are applied according to precedence and associativity rules, to be
specified by the user. This project also seeks to support *group symbols* which are
not strictly speaking operators, but allow for syntactical manipulation of precedence
beyond that specified by the operators alone.

## Progress
Currently, the supported features are:
   - Arbitrary precedence classes and associativity rules within those classes.
   - Support for infix binary and prefix unary operations

Active developments is going on with:
   - Grouping symbols, under branch 'group'

Interesting paths of development might include:
   - Support for more interesting operator placement (like infix and postfix for
     binary operations, like A(B) or A[B] ).