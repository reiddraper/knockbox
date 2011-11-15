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

(ns knockbox.sets.grow-only
  "This is an implementation of a
  state-based grow-only
  set data type. Operations are limited
  to set-addition, and the merge algorithm
  is simply the union of the two sets.")

(deftype Gset
  "Create a new grow-only set,
  optionally accepting a starting
  set"
  [items]

  (add [items item] (.Gset (conj items item)))

(defn merge
  "Merge two sets together"
  [a b]
  (clojure.set/union a b))

(defn items
  "Return all of the items in the set"
  [s]
  (seq s))

(defn exists?
  "Check for the existence of
  a particular item in the set"
  [s item]
  (boolean (s item)))
