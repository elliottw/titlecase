(use 'noir.core 'hiccup.page-helpers 'hiccup.form-helpers)
(require '[noir.validation :as vali])
(require '[noir.response :as resp])

(defpartial layout [& content]
  (html5
    [:head
     [:title "Forms"]]
    [:body
     content]))
