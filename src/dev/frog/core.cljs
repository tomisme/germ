(ns dev.frog.core)

(defprotocol SimpleFrog
  (croak [frog])
  (listen [frog])
  (look [frog])
  (jump [frog])
  ;;
  (get-ears [frog])
  (get-eyes [frog])
  (get-mouth [frog])
  (get-nose [frog])
  (get-legs [frog])
  (get-skeleton [frog]))

(defn read-frogfile []
  (let []
    {}))
