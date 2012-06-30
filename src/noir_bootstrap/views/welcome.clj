(ns noir-bootstrap.views.welcome
  (:require [noir-bootstrap.views.common :as common])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]))

(def short-words
    '("a" "an" "and" "as" "at" "but" "by" "en" "for" "if" "in" "n" "of" "on" "or" "the" "to" "v" "v." "vs" "vs." "via"))
  (def test-cases
      '(
            "1time is iPhone a weird word"
            "Just a normal sentence case phrase"
            "a lowercase phrase should break it off of"
            "For Step-by-Step Directions Email someone@gmail.com"
            "2lmc Spool: 'Gruber on OmniFocus and Vapo(u)rware'"
            "Your Hair[cut] Looks (Nice)"
            "People Probably Won't Put http://foo.com/bar/ in Titles"
            "Scott Moritz and TheStreet.com's Million iPhone La-La Land"
            "Scott Moritz and TheStreet.com's Million iPhone La-la Land"
            "Notes and Observations Regarding Apple's Announcements From 'The Beat Goes On' Special Event"
            "Read markdown_rules.txt to Find Out How _Underscores Around Words_ Will Be Interpretted"
            "Read markdown_rules.txt to Find Out How _underscores Around Words_ Will Be Interpretted"
            "(iPhone) is weird as of"
            "'By the Way, Small Word at the Start but Within Quotes.'"
            "Small Word at End Is Nothing to Be Afraid Of"
            "This v That"
            "This vs That"
            "The SEC's Apple Probe: What You Need to Know"
            "Starting Sub-Phrase With a Small Word: A Trick, Perhaps?"
            "Sub-Phrase With a Small Word in Quotes: 'A Trick, Perhaps?'"
            "\"Nothing to Be Afraid Of?\""
            "a better Try-n-Save try-n-save try-N-save"))


(defn cap-first-alpha [word]
  (if (re-seq #"[a-zA-Z0-9]" (str (first word)))
    (clojure.string/capitalize word)
    (str (first word) (clojure.string/capitalize (second word)) (subs word 2))))

(defn in-list? [word word-list]
  "takes in a word and a list of words and returns true if word is in list"
  (if (empty? word-list)
    false
    (if (= word (first word-list))
      true
      (in-list? word (rest word-list)))))

(defn camel? [word]
  (if (and (Character/isLetter (first word)) (re-seq #"[A-Z]" (subs word 1)))
    true
    false))

(defn split-n-cap [word-with-dashes]
  "capitalizes all the bits within a dashed word"
  (clojure.string/join "-"
    (map 
      (fn [x] (clojure.string/capitalize x)) 
      (clojure.string/split word-with-dashes #"[-]"))))

(defn starts-w-container? [word]
  (if 
    (or 
      (= \' (first word)) 
      (= \" (first word)) 
      (= \_ (first word)) 
      (= \( (first word)))
    true
    false))


(defn end-cap [word]
  "takes in a single word and returns proper capitalization for first or last word in title"
  (if (or (camel? word) (camel? (subs word 1)))
    word
    (if (starts-w-container? word)
      (str (first word) (clojure.string/capitalize (second word)) (subs word 2))
      (str (clojure.string/capitalize (first word)) (subs word 1)))))


(defn  cond-cap [wordstring]
  "conditionally capitalizes words in the middle of a title"
  (let 
    [first-word (first wordstring)
     first-letter (first (first wordstring))
     second-letter (second first-word)]
    (if (not (empty? wordstring))
      (cond 
        (in-list? first-word short-words)
        (do (println "short-word" first-word) (cons first-word (cond-cap (rest wordstring))))
        (camel? first-word)         
        (do (println "camel" first-word) (cons first-word (cond-cap (rest wordstring))))
        (in-list? "." (re-seq #"[a-z.]" first-word))
        (do (println "dot" first-word) (cons first-word (cond-cap (rest wordstring))))
        (in-list? "-" (re-seq #"[a-z-]" first-word))
        (do (println "dash" first-word) (cons (split-n-cap first-word) (cond-cap (rest wordstring))))
        (starts-w-container? first-word)
        (do
          (println "container" first-word " -> " (subs first-word 1) "camel? " (camel? (subs first-word 1)))
          (if (camel? (subs first-word 1))
            (do (println "still camel" first-word) (cons first-word (cond-cap (rest wordstring))))
            (cons 
              (str 
                first-letter
                (clojure.string/capitalize second-letter)
                (subs first-word 2))
              (cond-cap (rest wordstring)))))
        :else
        (do (println "normal" first-word) (cons (clojure.string/capitalize first-word) (cond-cap (rest wordstring))))))))




(defpage "/welcome" []
  (common/layout
    [:p "testing 423"]))

(use 'noir.core 'hiccup.page-helpers 'hiccup.form-helpers)
(require '[noir.validation :as vali])
(require '[noir.response :as resp])

(defpartial layout [& content]
  (html5
    [:head
      [:title "Forms"]]
    [:body
      content]))


(defpartial user-fields [{:keys [heading]}]
  (text-field {:placeholder "Type a title here"} "heading" heading))

(defpage "/" {:as title}
  (common/layout
    (form-to {:class "well"} [:post "/"]
      (user-fields title)
      [:br]
      [:button.btn-primary {:type "submit"} "Convert to Titlecase"])))

(defn valid? [{:keys [firstname lastname]}]
  true)

(defpage [:post "/"] {:as title}
 (if (valid? title)
    (common/layout
      [:div.well
        [:h3
         (interpose " " (cond-cap (clojure.string/split (title :heading) #"\s")))]])
    (render "/" title)))

(defpage "/comparison" []
  (common/layout
    [:div.row
      [:div.span3 [:h3 "Gruber's Ideal Title"]]
      [:div.span3 [:h3 "Gruber's Perl Results"]]
      [:div.span3 [:h3 "Gouch's Javascript"]]
      [:div.span3 [:h3 "Titlecase.org"]]]
    [:div.row [:br] ]
    [:div.row
      [:div.span3 "For Step-by-Step Directions Email someone@gmail.com"]
      [:div.span3 [:font {:color "red"} "For Step-by-Step Directions Email Someone@gmail.com"]]
      [:div.span3 [:font {:color "blue"} "For Step-by-Step Directions Email someone@gmail.com"]]
      [:div.span3 [:font {:color "blue"} "For Step-by-Step Directions Email someone@gmail.com"]]]
    [:br]
    [:div.row
      [:div.span3  "Scott Moritz and TheStreet.com’s Million iPhone La‑La Land"]
      [:div.span3 [:font {:color "red"} "Scott Moritz and TheStreet.Com’s Million iPhone La‑La Land"]]
      [:div.span3 [:font {:color "red"} "Scott Moritz and TheStreet.com’s Million iPhone La‑la Land"]]
      [:div.span3 [:font {:color "blue"} "Scott Moritz and TheStreet.com’s Million iPhone La‑La Land"]]]
    [:br]
    [:div.row
      [:div.span3 "For Step-by-Step Directions Email someone@gmail.com"]
      [:div.span3 [:font {:color "red"} "For Step-by-Step Directions Email Someone@gmail.com"]]
      [:div.span3 [:font {:color "blue"} "For Step-by-Step Directions Email someone@gmail.com"]]
      [:div.span3 [:font {:color "blue"} "For Step-by-Step Directions Email someone@gmail.com"]]]))

(defpage "/assumptions" []
  (common/layout
    [:p "All user entered capitalization is correct (e.g. The Doors will not be corrected to the Doors and iPhone will not be corrected to Iphone)"]))
