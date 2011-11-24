;; -------------------------------------------------------------------
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

(ns knockbox.sets.lww
  "This is an implementation of
  a state-based last-write-wins set.
  `add` and `remove` operations have associated
  timestamps that are used to resolve conflicts"

  (:import (clojure.lang IPersistentSet IPersistentMap
                         IFn IObj RT)
           (java.util Set)))

(defn- minus-deletes
  "Remove deletes with
  earlier timestamps
  than adds"
  [adds dels]
  (let [favor-deletes (fn [add delete] (if (>= delete add) nil add))
        no-deletes (merge-with favor-deletes
                        adds
                        (select-keys dels (keys adds)))
        no-nil (fn [a] (not= (get a 1) nil))]
    (map #(get % 0)
      (filter no-nil no-deletes))))

(deftype LWW [^IPersistentMap adds
              ^IPersistentMap dels]

  IPersistentSet 
  (disjoin [this k]
    (let [now (System/nanoTime)]
      (LWW. adds
        (assoc dels k now))))

  (cons [this k]
    (let [now (System/nanoTime)]
      (LWW.
        (assoc adds k now)
        dels)))

  (empty [this]
    (LWW. {} {}))

  (equiv [this other]
    (.equals this other))

  (get [this k]
    (if (> (get adds k) (get dels k))
      k
      nil))

  (seq [this]
    (keys 
      (minus-deletes adds dels)))

  (count [this]
    (count (seq this)))

  IObj
  (meta [this]
    (.meta ^IObj adds))

  (withMeta [this m]
    (LWW. (.withMeta ^IObj adds m)
          dels))

  Object
  (hashCode [this]
    (hash (set (seq this))))

  (equals [this other]
    (or (identical? this other)
        (and (instance? Set other)
             (let [^Set o (cast Set other)]
               (and (= (count this) (count o))
                    (every? #(contains? % o) (seq this)))))))

  (toString [this]
    "an string")

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
    dest))

(defn lww [] (LWW. {} {}))
