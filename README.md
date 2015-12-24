Simple word chain builder in Clojure using simulated annealing
===============================================================

Jim Van Donsel
December 2015

Simple and naive simulated annealing example to find a chain of words
linking a start and end word, each differing by one letter.

This isn't a very good application of simulated annealing since
here our goodness metric is perfect, i.e. we know when we've reached
the end word.

As an example, after many trials it found a chain of 16 linked words between
"apple" and "cider" as:

[apple ample amole anole ankle ankee anker inker inter enter ender eider bider rider hider cider]

The dictionary used here is Apple's, located at /usr/share/dict/words



