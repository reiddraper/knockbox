(ns knockbox.sets.gsets
  "This is an implementation of a
  state-based grow-only
  set data type. Operations are limited
  to set-addition, and the merge algorithm
  is simply the union of the two sets.")

(defn gset
  "Create a new grow-only set,
  optionally accepting a starting
  set"
  [& args]
  (or (first args) #{}))

(defn add
  "Add an item to a set"
  [s item]
  (conj s item))

(defn merge
  "Merge two sets together"
  [a b]
  (clojure.set/union a b))

(defn items
  "Return all of the items in the set"
  [s]
  (seq s))

(defn exists?
  "Check for the existence of
  a particular item in the set"
  [s item]
  (boolean (s item)))
