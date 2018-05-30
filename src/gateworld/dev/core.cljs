(ns gateworld.dev.core
  (:require
   [devcards.core]
   [gateworld.client.core]
   [gateworld.client.db]
   [gateworld.client.lang]
   [gateworld.client.views]
   [gateworld.client.views.combat]
   [gateworld.client.views.editor]
   [gateworld.client.views.intro]
   [gateworld.client.views.map]
   [gateworld.rules.core]
   [gateworld.rules.effects]))

(devcards.core/start-devcard-ui!)
