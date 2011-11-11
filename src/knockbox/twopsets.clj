(ns knockbox.twopsets
    "")

(defstruct kb2pset :adds :dels)

(defn twopset [& args]
    "Create a new 2p-set,
    optionally accepting a starting
    set"
    (let [initial (or (first args) #{})]
        (struct-map kb2pset
                    :adds initial
                    :dels #{})))

(defn add [s item]
    "Add an item to a set"
    (assoc s :adds
        (conj (:adds s) item)))

(defn remove [s item]
    "Remove an item from a set"
    (println (s :adds))
    (if ((s :adds) item) 
        (assoc s :dels
            (conj (s :dels) item))))

(defn merge [a b]
    "Merge two sets together"
    (let [adds (clojure.set/union
                    (a :adds) (b :adds))
          dels (clojure.set/union
                    (a :dels) (b :dels))]
        (struct-map kb2pset :adds adds :dels dels)))

(defn exists? [s item]
    "Check for the existence
    if a particular item in the
    set"
    (and ((s :adds) item) ((s :dels) item)))

(defn items [s]
    "Return all of the items in
    the set that haven't been
    deleted"
    (clojure.set/difference (s :adds) (s :dels)))
