(ns knockbox.gsets
    "grow-only sets")

(defn gset [& args]
    "Create a new grow-only set,
    optionally accepting a starting
    set"
    (or args #{}))

(defn add [s item]
    "Add an item to a set"
    (conj s item))

(defn merge [a b]
    "Merge two sets together"
    (clojure.set/union a b))
