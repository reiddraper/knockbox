(ns knockbox.set)

(defstruct kbset :data :ops)

(defn set [& args]
    (struct-map kbset
        :data (or (get args 0) #{})
        :ops (or (get args 1) (list))))

(defn add [s item]
    (let [op [conj item]
          ops (:ops s)]
            (struct-map kbset
                :data (:data s)
                :ops (conj ops op))))

(defn doit [s]
    (let [ops  (:ops s)
          data (:data s)
          func (fn [acc elem] (apply (first elem) acc (rest elem)))
          updated (reduce func data ops)]
            (println data ops)
            (struct-map kbset
                :data updated
                :ops ops)))
