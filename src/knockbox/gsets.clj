(ns knockbox.gsets
    "This is an implementation of the grow-only
    set data type. Operations are limited
    to set-addition, and the merge algorithm
    is simply the union of the two sets.")

(defn gset
    "Create a new grow-only set,
    optionally accepting a starting
    set"
    [& args]
    (or args #{}))

(defn add
    "Add an item to a set"
    [s item]
    (conj s item))

(defn merge
    "Merge two sets together"
    [a b]
    (clojure.set/union a b))
