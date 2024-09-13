(ns file-picker.core
  (:require
    [clojure.java.io :as io]
    [clojure.string :as str]
    [io.github.humbleui.ui :as ui])
  (:import
    [java.io File]))

(ui/defcomp file-list []
  (let [*dir (ui/signal
               (io/file (System/getProperty "user.home")))]
    (fn []
      [ui/center
       [ui/label "Hello, world!"]])))

(ui/defcomp app []
  [ui/rect {:paint {:fill "EEE"}}
   [ui/padding {:padding 10}
    [ui/column {:gap 10}
     ^:stretch
     [ui/rect {:paint {:fill "FFF"}}
      [file-list]]
     [ui/align {:x :right}
      [ui/row {:gap 10}
       [ui/button {:style :default} "Open"]
       [ui/button "Cancel"]]]]]])

(defn -main [& args]
  (ui/start-app!
    (def window
      (ui/window
        {:title    "File picker"
         :mac-icon "resources/icon.icns"}
        #'app))))

(comment
  ;; Start app
  (-main)
  
  ;; View docs
  ((requiring-resolve 'io.github.humbleui.docs/open!)))
