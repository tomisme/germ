(ns gateworld.client.events
  (:require
   [gateworld.client.db :as db]
   [gateworld.rules.core :as rules]
   [gateworld.rules.effects :as rules-effects]
   [re-frame.core :as rf]))


(rf/reg-event-db
  :initialise
  (fn [_ _]
    db/initial-db))


(rf/reg-event-db
  :start-combat-practise
  (fn [state _]
    (-> state
        (assoc :view :combat)
        (assoc :story ["Started practise combat"])
        (assoc :combat-state
               (let [rock-card {:name "Rock"}
                     blob-card {:name "Blob"
                                :attack 1
                                :toughness 1}]
                 (-> rules/empty-combat-state
                     (rules-effects/add-char rules/empty-char)
                     (rules-effects/add-char rules/empty-char)
                     (rules-effects/give-char-permanent 0 rock-card)
                     (rules-effects/give-char-permanent 0 blob-card)
                     (rules-effects/give-char-permanent 0 blob-card)
                     (rules-effects/give-char-permanent 1 blob-card)
                     #_(rules-effects/give-char-in-hand 0 rock-card)))))))


(rf/reg-event-db
  :combat/sacrifice-permanent
  (fn [state [_ char-idx perm-idx]]
    (-> state
        (update-in [:combat-state]
                   rules-effects/add-effect
                   {:type :sac-perm
                    :char-idx char-idx
                    :perm-idx perm-idx})
        (update-in [:combat-state]
                   rules-effects/resolve-next-effect))))
