(ns dev.gran
  (:require
    [reagent.core :as r]
    [promesa.core :as p]
    [goog.string :as goog-string]
    [goog.crypt :as goog-crypt]
    [goog.crypt.Sha256 :as Sha256])
  (:import
    [goog.testing PseudoRandom])
  (:require-macros
    [devcards.core :refer [defcard defcard-rg]]))

(defcard resources
  "
  Tools
  - https://github.com/fluree/fluree.crypto/blob/main/src/fluree/crypto.cljc

  CapTP
  - http://erights.org/elib/distrib/captp/index.html
  - https://docs.racket-lang.org/goblins/tutorial.html
  - https://gitlab.com/spritely/goblins/-/blob/dev/goblins/ocapn/captp.rkt
  - http://erights.org/elib/distrib/captp/index.html
  - https://capnproto.org/rpc.html
  - https://github.com/sandstorm-io/capnproto/blob/master/c++/src/capnp/rpc.capnp
  - http://erights.org/data/serial/jhu-paper/intro.html

  \"Chris figuring out how CapTP works\"
  - https://groups.google.com/g/cap-talk/c/xWv2-J62g-I
  - https://groups.google.com/g/cap-talk/c/-JYtc-L9OvQ
  - https://dustycloud.org/tmp/captp-handoff-musings.org.txt
  - https://dustycloud.org/misc/3vat-handoff-scaled.jpg
  ")


; goal: alice gives bob the capability to increment :a

; brains should be in vats right? What if the brains were the vats too?

(def alice
  {:brain {:a 1
           :b 42}
   :caps {:alice/inc-a #(update % :a inc)}
   :exports {1 'bob}
   :imports {}})

(def bob
  {:state {}
   :caps {}
   :exports {}
   :imports {1 'alice}})

(defcard alice alice)
(defcard bob bob)