(ns knockbox.sets.twopsets
  "This is an implementation of a
  state-based
  Two-Phase set data type.
  This data type allows deletes,
  with the limitation that items
  can only be deleted that are in
  the set (the local replica) and
  that items can not be added back
  once they've been deleted. The merge
  function simply takes the set union
  of each replica's `adds` and `deletes`.")

(defstruct kb2pset :adds :dels)

(defn twopset
  "Create a new 2p-set,
  optionally accepting a starting
  set"
  [& args]
  (let [initial (or (first args) #{})]
    (struct-map kb2pset
                :adds initial
                :dels #{})))

(defn add
  "Add an item to a set"
  [s item]
  (assoc s :adds
    (conj (:adds s) item)))

(defn remove
  "Remove an `item` from the set `s`.
  `item` must already be in the set."
  [s item]
  (assoc s :dels
    (conj (s :dels) item)))

(defn merge
  "Merge two sets together"
  [a b]
  (let [adds (clojure.set/union
                (a :adds) (b :adds))
        dels (clojure.set/union
                (a :dels) (b :dels))]
    (struct-map kb2pset :adds adds :dels dels)))

(defn exists?
  "Check for the existence
  of a particular item in the
  set"
  [s item]
  (boolean 
    (and ((s :adds) item) (not ((s :dels) item)))))

(defn items
  "Return all of the items in
  the set that haven't been
  deleted"
  [s]
  (seq
    (clojure.set/difference (s :adds) (s :dels))))
