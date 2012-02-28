; -------------------------------------------------------------------
;; Copyright (c) 2011 Basho Technologies, Inc.  All Rights Reserved.
;;
;; This file is provided to you under the Apache License,
;; Version 2.0 (the "License"); you may not use this file
;; except in compliance with the License.  You may obtain
;; a copy of the License at
;;
;;   http://www.apache.org/licenses/LICENSE-2.0
;;
;; Unless required by applicable law or agreed to in writing,
;; software distributed under the License is distributed on an
;; "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
;; KIND, either express or implied.  See the License for the
;; specific language governing permissions and limitations
;; under the License.
;;
;; -------------------------------------------------------------------

(in-ns 'knockbox.sets)

(import java.util.UUID)

(defn- uuid []
  (.toString (java.util.UUID/randomUUID)))

(defn- gets
  "Just like `get` but with #{} as the default
  notfound value"
  [coll item]
  (get coll item #{}))

(deftype ObservedRemoveSet [^IPersistentMap adds
                            ^IPersistentMap dels]

  IPersistentSet 
  (disjoin [this k]
    (let [new-del-value (clojure.set/union (gets adds k) (gets dels k))
          new-adds-map  (dissoc adds k)
          new-dels-map  (assoc dels k new-del-value)]
      (ObservedRemoveSet. new-adds-map new-dels-map)))

  (cons [this k]
    (let [id (uuid)
          new-adds-value (clojure.set/union #{id} (gets adds k))
          new-adds (assoc adds k new-adds-value)]
      (ObservedRemoveSet. new-adds dels)))

  (empty [this]
    (ObservedRemoveSet. {} {}))

  (equiv [this other]
    (.equals this other))

  (get [this k]
    (let [add-value (gets adds k)
          del-value (gets dels k)
          diff (clojure.set/difference add-value del-value)]
      (if (> (count diff) 0)
        k
        nil)))

  (seq [this]
    (let [candidates (keys adds)
          values (map #(get this %) candidates)
          no-nil (filter (comp not nil?) values)]
      (seq no-nil)))
          

  (count [this]
    (count (seq this)))

  IObj
  (meta [this]
    (.meta ^IObj adds))

  (withMeta [this m]
    (ObservedRemoveSet. (.withMeta ^IObj adds m)
          dels))

  Object
  ;; TODO:
  ;; need to come up with a
  ;; better hash func than this
  (hashCode [this]
    (hash (set (seq this))))

  (equals [this other]
    (or (identical? this other)
        (and (instance? Set other)
             (let [^Set o (cast Set other)]
               (and (= (count this) (count o))
                    (every? #(contains? o %) (seq this)))))))

  (toString [this]
    (.toString (seq this)))

  Set
  (contains [this k]
    (boolean (get this k)))

  (containsAll [this ks]
    (every? identity (map #(contains? this %) ks)))

  (size [this]
    (count this))

  (isEmpty [this]
    (= 0 (count this)))

  (toArray [this]
    (RT/seqToArray (seq this)))

  (toArray [this dest]
    (reduce (fn [idx item]
              (aset dest idx item)
              (inc idx))
            0, (seq this))
    dest)

  ;; this is just here to mark
  ;; the object as serializable
  Serializable

  IFn
  (invoke [this k]
    (get this k))

  java.lang.Iterable
  (iterator [this]
    (clojure.lang.SeqIterator. (seq this)))

  Resolvable 
  (resolve [this other]
    ;; TODO:
    ;; this is another opportunity to prune
    ;; the uuids in adds if they are in dels
    (let [new-adds (clojure.set/union adds (.adds other))
          new-dels (clojure.set/union dels (.dels other))]
      (ObservedRemoveSet. new-adds new-dels)))

  cheshire.custom/JSONable
  (to-json [this jsongen]
    (let [m {:type "or-set"}
          rfn (fn [acc elem]
                (let [a (vec (clojure.core/get adds elem))
                      d (vec (clojure.core/get dels elem))
                      ea [elem a]
                      vect (if d (conj ea d) ea)]
                  (conj acc vect)))
          elems (reduce rfn [] (clojure.set/union (keys adds)
                                                  (keys dels)))]
      (.writeRaw jsongen (cheshire.core/generate-string
                           (assoc m :e elems))))))

(defn observed-remove [] (ObservedRemoveSet. {} {}))

(defmethod knockbox.core/handle-json-structure "or-set"
  ;TODO
  ;need to figure out
  ;how to deal with
  ;strings vs. keywords
  [obj]
  (let [rfn (fn [[a r] [elem a-ids & r-ids]]
              [(if a-ids (assoc a (keyword elem) (set a-ids)) a)
               (if (seq r-ids) (assoc r (keyword elem) (set (first r-ids))) r)])
        [a r] (reduce rfn [{} {}] (:e obj))]
    (ObservedRemoveSet. a r)))

(defmethod print-dup knockbox.sets.ObservedRemoveSet [o w]
  (.write w (str "#=(" (.getName ^Class (class o)) ". " (.adds o) " " (.dels o) ")")))
