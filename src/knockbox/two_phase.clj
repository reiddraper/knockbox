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

(in-ns 'knockbox.set)

(defn twop-minus-deletes [adds dels]
  (clojure.set/difference
    adds
    dels))

(deftype TwoPhaseSet [^IPersistentSet adds
                      ^IPersistentSet dels]

  IPersistentSet 
  (disjoin [this k]
    (if (contains? adds k)
      (TwoPhaseSet. adds (conj dels k))
      ;; TODO:
      ;; should this return nil or this?
      this))

  (cons [this k]
    (TwoPhaseSet.
      (conj adds k)
      dels))

  (empty [this]
    (TwoPhaseSet. #{} #{}))

  (equiv [this other]
    (.equals this other))

  (get [this k]
    (if (get dels k)
      nil
      (get adds k)))

  (seq [this]
    (seq
      (twop-minus-deletes adds dels)))

  (count [this]
    (count (seq this)))

  IObj
  (meta [this]
    (.meta ^IObj adds))

  (withMeta [this m]
    (TwoPhaseSet. (.withMeta ^IObj adds m)
          dels))

  Object
  (hashCode [this]
    (hash (twop-minus-deletes adds dels)))

  (equals [this other]
    (or (identical? this other)
        (and (instance? Set other)
             (let [^Set o (cast Set other)]
               (and (= (count this) (count o))
                    (every? #(contains? % o) (seq this)))))))

  (toString [this]
    (.toString (twop-minus-deletes adds dels)))

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

  Resolvable 
  (resolve [this other]
    (let [new-adds (clojure.set/union adds (.adds other))
          new-dels (clojure.set/union dels (.dels other))]
      (TwoPhaseSet. new-adds new-dels))))

(defn two-phase [] (TwoPhaseSet. #{} #{}))
