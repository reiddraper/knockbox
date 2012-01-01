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

(ns knockbox.maps
  (:require clojure.set)
  (:require knockbox.registers)
  (:import (clojure.lang IObj)
           (java.io Serializable))
  (:refer-clojure :exclude [resolve])
  (:use [knockbox.resolvable]))

(deftype LWWMap [storage metad]
  ;; use the `metad` var to carry
  ;; the metadata information
  IObj
  (meta [this]
    metad)

  (withMeta [this m]
    (LWWMap. storage m))

  clojure.lang.IPersistentMap
  (assoc [this k v]
    (let [reg (knockbox.registers/lww v)
          new-storage (assoc storage k reg)]
      (LWWMap. new-storage metad)))

  (assocEx [this k v]
    (if (get storage k)
      (throw (RuntimeException. "Key already present"))
      (assoc this k v)))

  (without [this k]
    (LWWMap. (dissoc storage key) metad))

  Serializable)

(defn lww [] (LWWMap. {} {}))
