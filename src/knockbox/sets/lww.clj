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
  timestamps that are used to resolve conflicts")

(defstruct kblwwset :adds :dels)

(defn- hash-max
  "Merge two hashes, taking the
  max value from keys in both hashes"
  [a b]
  (let [f (fn [a b] (max a b))]
    (merge-with f a b)))

(defn- minus-deletes
  "Remove deletes with
  earlier timestamps
  than adds"
  [s]
  (let [favor-deletes (fn [add delete] (if (>= delete add) nil add))
        no-deletes (merge-with favor-deletes
                        (s :adds)
                        (select-keys (s :dels) (keys (s :adds))))
        no-nil (fn [a] (not= (get a 1) nil))]
    (into {} (filter no-nil no-deletes))))
 
(defn lwwset
  "Create a new lww set,
  optionally accepting
  a starting set"
  [& args]
  (let [initial (or (first args) (hash-map))]
    (struct-map kblwwset
                :adds initial
                :dels (hash-map))))

(defn add
  "Add an item to a set"
  [s item]
  (let [now (System/nanoTime)]
    (assoc s :adds
      (assoc (s :adds) item now))))

(defn remove
  "Remove an item from a set"
  [s item]
  (let [now (System/nanoTime)]
    (assoc s :dels
      (assoc (s :dels) item now))))

(defn exists?
  "Check for the existence
  of a particular item in the set"
  [s item]
  (boolean ((minus-deletes s) item)))

(defn items
  "Return all of the items 
  that haven't been deleted in
  the set"
  [s]
  (keys (minus-deletes s)))

(defn merge
  "Merge two sets together"
  [a b]
  (let [adds (hash-max (a :adds) (b :adds))
        dels (hash-max (a :dels) (b :dels))]
    (struct-map kblwwset :adds adds :dels dels)))
