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

(ns knockbox.registers
  (:require [clojure.set])
  (:import (clojure.lang IObj)
           (java.io Serializable))
  (:refer-clojure :exclude [resolve])
  (:use [knockbox.resolvable]))

(deftype LWWRegister [value timestamp metad]
  ;; use the `__meta` var to carry
  ;; the metadata information
  IObj
  (meta [this]
    metad)

  (withMeta [this m]
    (LWWRegister. value timestamp m))

  Resolvable
  (resolve [this other]
    (if (> timestamp (.timestamp other))
      this
      other))
  
  Serializable)

(defn lww [value] (LWWRegister. value (System/nanoTime) {}))

(defmethod print-dup knockbox.registers.LWWRegister [o w]
  (.write w (str "#=(" (.getName ^Class (class o)) ". " (.value o) " " (.timestamp o) " " (.metad o) ")")))
