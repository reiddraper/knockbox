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

  (:use [ordered.common :only [Compactable compact change!]])
  (:import (clojure.lang IPersistentSet ITransientSet IEditableCollection
                         IPersistentMap ITransientMap ITransientAssociative
                         IPersistentVector ITransientVector
                         Associative SeqIterator Reversible IFn IObj)
           (java.util Set Collection)))

(defn- minus-deletes
  "Remove deletes with
  earlier timestamps
  than adds"
  [adds dels]
  (let [favor-deletes (fn [add delete] (if (>= delete add) nil add))
        no-deletes (merge-with favor-deletes
                        adds
                        (select-keys dels (keys adds))))
        no-nil (fn [a] (not= (get a 1) nil))]
    (into {} (filter no-nil no-deletes))))

(defn- hash-max
  "Merge two hashes, taking the
  max value from keys in both hashes"
  [a b]
  (let [f (fn [a b] (max a b))]
    (merge-with f a b)))

(deftype LWW [^IPersistentMap adds
              ^IPersistentMap dels]

  IPersistentSet 
  (disjoin [this key]
    (let [now (System/nanoTime)]
      (LWW. adds
        (assoc dels item now))))

  (cons [this k]
    (let [now (System/nanoTime)]
      (LWW.
        (assoc adds item now)
        dels)))

  (seq [this]
    (keys (minus-deletes adds dels)))

  (empty [this]
    (LWW. {} {}))

  (equiv [this other]
    (.equals this other))

  (get [this k]
    (if (> (get adds k) (get dels k)
      k
      nil)))

  (count [this]
    (count (seq this)))

(defn exists?
  "Check for the existence
  of a particular item in the set"
  [s item]
  (boolean ((minus-deletes s) item)))


(defn merge
  "Merge two sets together"
  [a b]
  (let [adds (hash-max (a :adds) (b :adds))
        dels (hash-max (a :dels) (b :dels))]
    (struct-map kblwwset :adds adds :dels dels)))
