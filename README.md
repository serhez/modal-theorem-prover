# Modal Logic Theorem Prover

This is my **Bachelor thesis at University College London (UCL), supervised by Prof. Robin Hirsch**. It is a **theorem prover** compatible with Modal Logics D, T, B, S4, as well as Linear Modal Logic. This prover was also used to carry out a **study on the validity rate of Modal formulae** with different attributes and constraints.

You will find the my **thesis paper** at `paper.pdf`, which includes the report on the work you can find on this repository, as well as the validity rate study and other discussions.

## Manual

### Grammar

This `formulaArray` prover uses standard Modal Logic syntax. It is recursively defined as the following:

- $\text{prop} = \{a : a \text{ is a string }\}$ \ $\{b : b \text{ is "T" or "F" or a string such that any of its substrings are ~, [], <>, \&, |, ->, <->$, $";", ":", ",", ")" or "(" }\}$
- $\text{fmla} = \text{T | F | prop | ¬fmla | (fmla ∧ fmla) | (fmla ∨ fmla) | (fmla → fmla) | (fmla ↔ fmla) | □fmla | ◊fmla}$

Please, make sure you are making use of parentheses correctly, as specified above.
The "T" and "F" literals are used to express truth and falsehood. They are not essential, but useful. For example: "T" is equivalent to "(p|~p)"; and "F" is equivalent to "(p&~p)".

### Syntax

The syntax you must use when writing your formulas is the following (format: "what you want to write" = "how you should write it"):

 - $¬A =$ ~A
 - $□A =$ []A
 - $◊A =$ <>A
 - $(A ∧ B) =$ (A & B)
 - $(A ∨ B) =$ (A | B)
 - $(A → B) =$ (A -> B)
 - $(A ↔ B) =$ (A <-> B)

You must also note that formulas in a `formulaArray` are separated by commas (",") and different `formulaArrays` to prove are separated by semicolons (";").
The last formula in a given `formulaArray` will be considered as the formula intended to be proven, and all previous formulas of the `formulaArray` will be considered as axioms.
Also, the use of spaces, new tabs, new lines, etc. is irrelevant (but surely helpful for the reader).

### Frame

At the beginning of the input file, before any `formulaArray` has been defined, you may include a Modal System specified by a set of frame conditions which will be applied to the `formulaArrays` in such input file. If not such system is specified, only the frame condition K will be applied.
The frame conditions currently supported by this `formulaArray` prover are the following:

- K = Kripke (no special conditions)
- T = Reflexive
- B = Symmetric
- D = Serial
- L = Linear
- 4 = Transitive

In order to specify your frame conditions, you may include the following construct (a string of your frame conditions surrounded by colons): `:frame_conditions:`, where frame_conditions is a string whose only characters are 'K', 'T', 'B', 'D' and '4', in any order but without repetition of any of them.

Note that some frame conditions are incompatible. In this version of the program, only L is incompatible with B and D (as serial frames are anti-symmetric and cannot be serial). Also, some frame conditions imply other frame conditions. For example, linearity implies transitivity.


### Usage

Create a file called "input.txt" and locate it within the folder "input". In it, write your `formulaArrays` as specified above. Next, run the Java program. Finally, find your results in the file "output.txt" in the folder named "output".
If a formula is deemed as "not recognized", you should check that particular formula as it is not following the syntax and/or grammar specified above. If you are confident your formula is correct, please report any bugs.
