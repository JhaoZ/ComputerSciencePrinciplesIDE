# ComputerSciencePrinciplesIDE
This is a custom made IDE for the AP Computer Science Principles syntax (which is mostly psuedocode). It is currently in development, but many features (and bugs!) are already implemented. It is programmed in Java.
 
The "help" button might not work due to hardcoded paths. (I'll fix that someday...)

In this repository, there are two examples of the AP CSP code (these are SearchList.csp and FindThePrimes.csp)

SearchList.csp will search a list for a value. FindThePrimes.csp will find prime numbers from 1 - 100. It essence, the AP CSP course won't test anything harding than the prime finding program, and thus this IDE can be used to run CSP code to check answers or get more familiar with the language.

Features (currently) Implemented:
1. Variable storing (dynamic, like python)
2. List storing (also dynamic) and getting list length
3. Accessing list elements with indexes (although the CSP curriculum has lists starting at index "1," it became too annoying for me to access a list from "1", so I just made it like all other languages, so lists start at index "0")
4. Loops (for loops to be exact)
5. If statements (else if doesn't work, but else does)
6. Evaulation (it can solve math and string operations, although result will be in floating point value if dealing with integers).

Features to be added:
1. Fixing the floating point value evalution so that values can be used in loops.
2. Specialized/for each loops
3. While loops (Repeat Until in csp code)
4. Calling and setting procedures with void or return values (hopefully with recursion included)
5. Else if
6. Sorts (not part of csp, but will be very useful)
7. Turtle (similiar to the python one)
8. Graphing
9. Better way of saving and loading files + keyboard shortcuts
10. Garbage collector, like Java
11. Fix bugs

To run the IDE, run the file "GUI.java"
