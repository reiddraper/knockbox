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

(ns knockbox.sets.two-phase
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
