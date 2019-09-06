(ns dev.dream.scratch
  (:require-macros
   [devcards.core :refer [defcard]]))

(def *state* #{{:dream/choice :a}})

(defcard state *state*)
