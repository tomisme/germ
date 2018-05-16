(ns gateworld-devcards.rules
  (:require
   [gateworld.rules.core :as rules])
  (:require-macros
   [devcards.core :refer [defcard]]))


(defcard sacrifice-permanent
  (let [char (assoc-in rules/empty-char-state
                        [:permanents]
                        [{:name "SacRock"} {:name "NoSacRock"}])
        s1 (rules/add-char rules/empty-combat-state char)
        s2 (rules/add-effect s1 {:type :sacrifice
                                 :pid 0
                                 :permanent-index 0})]
    (rules/resolve-next-effect s2)))
