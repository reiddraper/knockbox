(ns knockbox.test.sets
  (:require [knockbox.sets])
  (:use midje.sweet)
  (:use clojure.test))

(fact
  (let [s (knockbox.sets/observed-remove)]
    (contains? s :foo) => false))

(fact
  (let [s (knockbox.sets/observed-remove)
        f (conj s :foo)]
    (contains? f :foo) => true))

(fact
  (let [s (knockbox.sets/observed-remove)
        f (conj s :foo)]
    (contains? f :foo) => true))

(fact (contains? (conj (knockbox.sets/observed-remove) :foo) :foo) => true)

;(tabular
;  (fact "sets contain items properly"
;        (let [obs-rem (knockbox.sets/observed-remove)
;              lww (knockbox.sets/lww)
;              two-phase (knockbox.sets/two-phase)]
;          (contains? ?set ?item) => ?expected))
;        ?set    ?item   ?expected
;        lww     :foo    false)
;
