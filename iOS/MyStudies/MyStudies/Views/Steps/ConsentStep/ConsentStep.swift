// License Agreement for FDA MyStudies
// Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors. Permission is
// hereby granted, free of charge, to any person obtaining a copy of this software and associated
// documentation files (the &quot;Software&quot;), to deal in the Software without restriction, including without
// limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
// Software, and to permit persons to whom the Software is furnished to do so, subject to the following
// conditions:
// The above copyright notice and this permission notice shall be included in all copies or substantial
// portions of the Software.
// Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as
// Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
// THE SOFTWARE IS PROVIDED &quot;AS IS&quot;, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
// INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
// PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
// OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
// OTHER DEALINGS IN THE SOFTWARE.

import Foundation
import ResearchKit

let kConsentStepType = "type"

let kConsentStepTitle = "title"
let kConsentStepText = "text"
let kConsentStepDescription = "description"
let kConsentStepHTML = "html"
let kConsentStepURL = "url"
let kConsentStepVisualStep = "visualStep"

enum ConsentStepSectionType: String {
  case overview = "overview"
  case dataGathering = "dataGathering"
  case privacy = "privacy"
  case dataUse = "dataUse"
  case timeCommitment = "timeCommitment"
  case studySurvey = "studySurvey"
  case studyTasks = "studyTasks"
  case withdrawing = "withdrawing"
  case custom = "custom"

  /// Method to get IntegerValue for the Enum ans returns Integer specific to ORKConsent
  func getIntValue() -> Int! {
    switch self {
    case .overview:
      return 0
    case .dataGathering:
      return 1
    case .privacy:
      return 2
    case .dataUse:
      return 3
    case .timeCommitment:
      return 4
    case .studySurvey:
      return 5
    case .studyTasks:
      return 6
    case .withdrawing:
      return 7
    case .custom:
      return 8
    }
  }

}

class ConsentSectionStep {

  var type: ConsentStepSectionType?
  var title: String?

  var text: String?
  var description: String?

  var html: String?

  var url: String?

  var visualStep: Bool?

  /// Default Initializer
  init() {

    self.type = .overview
    self.title = ""
    self.text = ""

    self.description = ""
    self.html = ""

    self.url = ""
    self.visualStep = false

  }

  /// Initializer method which initializes all params
  /// - Parameter stepDict: Dictionary which contains all the properties of ConsentSection Step
  func initWithDict(stepDict: [String: Any]) {

    if Utilities.isValidObject(someObject: stepDict as AnyObject?) {

      if Utilities.isValidValue(someObject: stepDict[kConsentStepType] as AnyObject) {
        self.type = ConsentStepSectionType(
          rawValue: (stepDict[kConsentStepType] as? String)!
        )
      }

      if Utilities.isValidValue(someObject: stepDict[kConsentStepTitle] as AnyObject) {
        self.title = stepDict[kConsentStepTitle] as? String
      }
      if Utilities.isValidValue(someObject: stepDict[kConsentStepText] as AnyObject) {
        self.text = stepDict[kConsentStepText] as? String
      }
      if Utilities.isValidValue(someObject: stepDict[kConsentStepDescription] as AnyObject) {
        self.description = stepDict[kConsentStepDescription] as? String
      }
      if Utilities.isValidValue(someObject: stepDict[kConsentStepHTML] as AnyObject) {
        self.html = stepDict[kConsentStepHTML] as? String
        if let html = self.html {
          self.html = html.stringByDecodingHTMLEntities
          let regex = "<[^>]+>"
          if self.html?.stringByDecodingHTMLEntities.range(of: regex, options: .regularExpression) == nil {
            if let valReConversiontoHTMLfromHTML = html.stringByDecodingHTMLEntities.htmlToAttriString?.attriString2Html {
              
              if let attributedText =
                  valReConversiontoHTMLfromHTML.stringByDecodingHTMLEntities.htmlToAttriString, attributedText.length > 0 {
                self.html = attributedText.attriString2Html
              } else if let attributedText =
                          html.htmlToAttriString?.attriString2Html?.stringByDecodingHTMLEntities.htmlToAttriString,
                        attributedText.length > 0 {
                self.html = attributedText.attriString2Html
              } else {
                self.html = html
              }
            } else {
              self.html = html
            }
          }
        }
      }
      if Utilities.isValidValue(someObject: stepDict[kConsentStepURL] as AnyObject) {
        self.url = stepDict[kConsentStepURL] as? String
      }
      if Utilities.isValidValue(someObject: stepDict[kConsentStepVisualStep] as AnyObject) {
        self.visualStep = stepDict[kConsentStepVisualStep] as? Bool
      }
    }
  }

  /// Creates Consent Section and returns a ORKConsentSection instance based on the properties
  func createConsentSection() -> ORKConsentSection {

    let consentType: Int? = (self.type?.getIntValue())! >= 0 ? (self.type?.getIntValue()) : -1

    let consentSection: ORKConsentSection!

    if self.visualStep == true {

      consentSection = ORKConsentSection(type: ORKConsentSectionType(rawValue: consentType!)!)
    } else {
      consentSection = ORKConsentSection(type: ORKConsentSectionType.onlyInDocument)
    }
    consentSection.title = self.title!
    consentSection.summary = self.text!
    consentSection.customLearnMoreButtonTitle = LocalizableString.learnMore.localizedString
    if self.description?.isEmpty == false {
      consentSection.content = self.description!
    } else if self.html!.isEmpty == false {
      consentSection.htmlContent = self.html!
    } else if self.url!.isEmpty == false {
      consentSection.contentURL = URL(string: self.url!)
    }

    if self.type == .custom && self.visualStep == true {

      consentSection.customImage = #imageLiteral(resourceName: "task_img2")
    }

    return consentSection
  }

}
