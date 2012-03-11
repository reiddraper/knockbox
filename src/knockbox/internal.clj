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

(ns knockbox.internal
  (:require [clojure.string :as string])
  (:import java.util.UUID))

(def ^:private counter (atom 0N))
(def ^:private uuid (.toString (java.util.UUID/randomUUID)))

(defn next-count! []
  (swap! counter inc))


(defn- time-since-epoch! []
  (.getTime (java.util.Date.)))

(defn sorted-unique-id! []
  "Return unique ids that are roughly sorted
  by time. In the case that two ids are generated
  at the same time across JVMs, they unique because
  of the UUID. In the case two ids are generated
  at the same time on the same machine, there is
  also a counter
  Example:
  [1331496905454 \"327c4f9a-d3c8-453e-b332-8e04d1db0a2e\" 7N]"
  [(time-since-epoch!)
   uuid
   (next-count!)])

