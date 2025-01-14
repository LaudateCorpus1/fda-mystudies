// License Agreement for FDA MyStudies
// Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
// Copyright 2020 Google LLC
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
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

/// Consent Api Constants
let kConsentVisual = "consent"
let kConsentSharing = "sharing"
let kConsentReview = "review"
let kConsentVisualScreens = "visualScreens"

let kConsentVersion = "version"
let kConsentPdfContent = "content"

let kConsentTaskIdentifierText = "ConsentTask"

/// SharingConsent Api Constants
let kConsentSharingStepShortDesc = "shortDesc"
let kConsentSharingStepLongDesc = "longDesc"
let kConsentSharingSteplearnMore = "learnMore"
let kConsentSharingStepText = "text"
let kConsentSharingStepTitle = "title"
let kConsentSharingStepAllowWithoutSharing = "allowWithoutSharing"

/// ReviewConsent Api Constants
let kConsentReviewStepTitle = "title"
let kConsentReviewStepSignatureTitle = "signatureTitle"
let kConsentReviewStepSignatureContent = "reviewHTML"
let kConsentReviewStepReasonForConsent = "reasonForConsent"

/// Comprehension Api Constants
let kConsentComprehension = "comprehension"
let kConsentComprehensionQuestions = "questions"
let kConsentComprehensionCorrectAnswers = "correctAnswers"
let kConsentComprehensionCorrectAnswersKey = "key"
let kConsentComprehensionAnswer = "answer"
let kConsentComprehensionPassScore = "passScore"
let kConsentComprehensionEvaluation = "evaluation"

let kConsentSharePdfCompletionStep = "ConsentShareCompletionStep"
let kConsentViewPdfCompletionStep = "ConsentViewPdfCompletionStep"
let kConsentCompletionStepIdentifier = "ConsentFinalCompletionStep"

/// Completion Storyboard Segue
let kConsentSharePdfStoryboardId = "ConsentSharePdfStepViewControllerIdentifier"

let kConsentViewPdfStoryboardId = "ConsentPdfViewerStepViewControllerIdentifier"

/// Comprehenion Instruction Step Keys
let kConsentComprehensionTestTitle = "Comprehension"
let kConsentComprehensionTestText =
  "Let's do a quick and simple test of your understanding of this study."
let kComprehensionInstructionStepIdentifier = "ComprehensionInstructionStep"

/// Comprehension Completion Step Keys
let kComprehensionCompletionTitle = "Great job!"
let kComprehensionCompletionText =
  "You answered all of the questions correctly. Tap next to continue."
let kComprehensionCompletionStepIdentifier = "ComprehensionCompletionStep"

/// Consent Completion
let kConsentCompletionMainTitle = "Consent confirmed"
let kConsentCompletionSubTitle = "You can now start participating in the study"
let kSignaturePageContentText = "I agree to participate in this research study."

/// Signature Page
let kConsentSignaturePageContent = "signaturePageContent"
let kConsentSignaturePageTitle = "Participant"

enum ConsentStatus: String {
  case pending
  case completed
}

// MARK: ConsentBuilder Class

class ConsentBuilder {

  var consentSectionArray: [ORKConsentSection]

  var consentStepArray: [ORKStep]

  var reviewConsent: ReviewConsent?
  var sharingConsent: SharingConsent?

  var consentDocument: ORKConsentDocument?
  var version: String?
  var consentStatus: ConsentStatus?
  var consentHasVisualStep: Bool?
  static var currentConsent: ConsentBuilder?

  var consentResult: ConsentResult?

  var comprehension: Comprehension?

  /// Default Initializer method
  init() {
    consentSectionArray = []
    consentStepArray = []
    reviewConsent = ReviewConsent()
    sharingConsent = SharingConsent()
    consentResult = ConsentResult()
    consentDocument = ORKConsentDocument()
    version = ""
    consentHasVisualStep = false
    comprehension = Comprehension()
  }

  /// initializer method which initializes all params
  /// - Parameter metaDataDict: contains as Dictionaries for all the properties of Consent Step
  func initWithMetaData(metaDataDict: [String: Any]) {
    consentStatus = .pending
    consentHasVisualStep = false

    if Utilities.isValidObject(someObject: metaDataDict as AnyObject?) {
      if Utilities.isValidValue(someObject: metaDataDict[kConsentVersion] as AnyObject?) {
        version = (metaDataDict[kConsentVersion] as? String)!
      } else {
        version = "No_Version"
      }
      let visualConsentArray = (metaDataDict[kConsentVisualScreens] as? [[String: Any]])!

      if Utilities.isValidObject(someObject: visualConsentArray as AnyObject?) {
        for sectionDict in visualConsentArray {
          let consentSection: ConsentSectionStep? = ConsentSectionStep()
          consentSection?.initWithDict(stepDict: sectionDict)
          consentSectionArray.append((consentSection?.createConsentSection())!)
          consentHasVisualStep = true
        }
      }

      let consentSharingDict = (metaDataDict[kConsentSharing] as? [String: Any])!

      if !StudyUpdates.studyConsentUpdated {
        if Utilities.isValidObject(someObject: consentSharingDict as AnyObject?) {
          sharingConsent?.initWithSharingDict(dict: consentSharingDict)
        }
      }
      let reviewConsentDict = (metaDataDict[kConsentReview] as? [String: Any])!

      if Utilities.isValidObject(someObject: reviewConsentDict as AnyObject?) {
        reviewConsent?.initWithReviewDict(dict: reviewConsentDict)
      }

      let comprehensionDict = (metaDataDict[kConsentComprehension] as? [String: Any])!

      if Utilities.isValidObject(someObject: comprehensionDict as AnyObject?) {
        comprehension?.initWithComprehension(dict: comprehensionDict)
      }
    }
  }

  /// Return an instance of ORKVisualConsentStep
  func getVisualConsentStep() -> VisualConsentStep? {
    if consentSectionArray.count > 0 {
      let invisibleVisualConsents = consentSectionArray.filter { $0.type == .onlyInDocument }

      if invisibleVisualConsents.count == consentSectionArray.count {
        return nil
      } else {
        // create Visual Consent Step
        let visualConsentStep = VisualConsentStep(
          identifier: kVisualStepId,
          document: getConsentDocument()
        )
        return visualConsentStep
      }
    } else {
      return nil
    }
  }

  /// Return an ConsentDocument
  func getConsentDocument() -> ORKConsentDocument {
    if consentDocument != nil,
      Utilities.isValidObject(someObject: consentDocument?.sections as AnyObject?)
    {
      return consentDocument!
    } else {
      consentDocument = createConsentDocument()
      return consentDocument!
    }
  }

  /// Creates ConsentDocument and returns a ORKConsentDocument for VisualConsentStep and Review Step
  func createConsentDocument() -> ORKConsentDocument? {
    let consentDocument = ORKConsentDocument()

    if Utilities.isValidValue(someObject: "title" as AnyObject),
      Utilities.isValidValue(someObject: "signaturePageTitle" as AnyObject),
      Utilities.isValidValue(someObject: kConsentSignaturePageContent as AnyObject)
    {
      consentDocument.title = Study.currentStudy?.name
      consentDocument.signaturePageTitle = kConsentSignaturePageTitle
      consentDocument.signaturePageContent = kConsentSignaturePageContent

      consentDocument.sections = [ORKConsentSection]()

      if consentSectionArray.count > 0 {
        consentDocument.sections?.append(contentsOf: consentSectionArray)
      }

      let investigatorSignature = ORKConsentSignature(
        forPersonWithTitle: kConsentSignaturePageTitle,
        dateFormatString: "MM/dd/yyyy",
        identifier: "Signature"
      )

      consentDocument.addSignature(investigatorSignature)
      return consentDocument
    } else {
      return nil
    }
  }

  /// Retrives ComprehensionSteps
  func getComprehensionSteps() -> [ORKStep]? {
    if (comprehension?.questions?.count)! > 0 {
      var stepsArray: [ORKStep]? = [ORKStep]()
      for stepDict in (comprehension?.questions!)! {
        // create questionSteps
        let questionStep: ActivityQuestionStep? = ActivityQuestionStep()
        questionStep?.initWithDict(stepDict: stepDict)

        questionStep?.skippable = false
        stepsArray?.append((questionStep?.getQuestionStep())!)
      }
      return stepsArray
    } else {
      return nil
    }
  }

  /// Retrives ReviewConsentStep
  func getReviewConsentStep() -> ConsentReviewStep? {
    let reviewConsentStep: ConsentReviewStep?

    if Utilities.isValidValue(someObject: reviewConsent?.signatureContent as AnyObject) {
      // create Consent Document
      let consentDocument: ORKConsentDocument? = (getConsentDocument() as ORKConsentDocument)
      consentDocument?.htmlReviewContent = reviewConsent?.signatureContent
      consentDocument?.signaturePageContent = NSLocalizedString(
        kSignaturePageContentText,
        comment: ""
      )

      // Initialize review Step
      reviewConsentStep = ConsentReviewStep(
        identifier: kReviewTitle,
        signature: (getConsentDocument() as ORKConsentDocument).signatures?[0],
        in: getConsentDocument()
      )

    } else {
      // create Consent Document
      let consentDocument: ORKConsentDocument? = (getConsentDocument() as ORKConsentDocument)
      consentDocument?.signaturePageContent = NSLocalizedString(
        kSignaturePageContentText,
        comment: ""
      )

      // Initialize review Step
      reviewConsentStep = ConsentReviewStep(
        identifier: kReviewTitle,
        signature: consentDocument?.signatures?[0],
        in: consentDocument!
      )
    }

    if Utilities.isValidValue(someObject: reviewConsent?.signatureTitle as AnyObject) {
      reviewConsentStep?.text = reviewConsent?.title
    } else {
      reviewConsentStep?.text = ""
    }

    if Utilities.isValidValue(someObject: reviewConsent?.reasonForConsent as AnyObject) {
      reviewConsentStep?.reasonForConsent = (reviewConsent?.reasonForConsent)!
    } else {
      reviewConsentStep?.reasonForConsent = ""
    }

    return reviewConsentStep
  }

  /// RetrivesConsentSharingStep
  func getConsentSharingStep() -> ConsentSharingStep? {
    if Utilities.isValidValue(someObject: sharingConsent?.shortDesc as AnyObject),
      Utilities.isValidValue(someObject: sharingConsent?.longDesc as AnyObject),
      Utilities.isValidValue(someObject: sharingConsent?.learnMore as AnyObject)
    {
      // create shareStep
      let sharingConsentStep = ConsentSharingStep(
        identifier: kConsentSharing,
        investigatorShortDescription: (sharingConsent?.shortDesc)!,
        investigatorLongDescription: (sharingConsent?.longDesc)!,
        localizedLearnMoreHTMLContent: (sharingConsent?.learnMore)!
      )

      if Utilities.isValidValue(someObject: sharingConsent?.text as AnyObject) {
        sharingConsentStep.text = sharingConsent?.text
      }

      if Utilities.isValidValue(someObject: sharingConsent?.title as AnyObject) {
        sharingConsentStep.title = sharingConsent?.title
      }
      return sharingConsentStep
    } else {
      return nil
    }
  }

  /// Retrives ConsentTask
  func createConsentTask() -> ORKTask? {
    let visualConsentStep: VisualConsentStep? = getVisualConsentStep()
    let sharingConsentStep: ConsentSharingStep? = getConsentSharingStep()
    let reviewConsentStep: ConsentReviewStep? = getReviewConsentStep()

    let comprehensionSteps: [ORKStep]? = getComprehensionSteps()
    reviewConsentStep?.stepViewControllerClass()

    var stepArray: [ORKStep]? = Array()

    if visualConsentStep != nil {
      if consentHasVisualStep! {
        stepArray?.append(visualConsentStep!)
      } else {
        // means all steps are custom only included in documents and there are no Visual Step
        // Do Nothing
      }
    }

    // comprehension steps
    if comprehensionSteps != nil, (comprehensionSteps?.count)! > 0 {
      // adding Instruction Step for Comprehenion
      let comprehensionTestInstructionStep = EligibilityInstructionStep(
        identifier: kComprehensionInstructionStepIdentifier
      )
      comprehensionTestInstructionStep.text = kConsentComprehensionTestText

      comprehensionTestInstructionStep.title = kConsentComprehensionTestTitle
      stepArray?.append(comprehensionTestInstructionStep)

      // adding questionary
      for step in comprehensionSteps! {
        stepArray?.append(step)
      }

      // adding Completion Step
      let comprehensionCompletionStep = EligibilityInstructionStep(
        identifier: kComprehensionCompletionStepIdentifier
      )
      comprehensionCompletionStep.text = kComprehensionCompletionText

      comprehensionCompletionStep.title = kComprehensionCompletionTitle
      comprehensionCompletionStep.image = #imageLiteral(resourceName: "successBlueBig")
      stepArray?.append(comprehensionCompletionStep)
    }

    if sharingConsentStep != nil {
      stepArray?.append(sharingConsentStep!)
    }
    if reviewConsentStep != nil {
      stepArray?.append(reviewConsentStep!)
    }

    // PDF Generation Step
    let consentCompletionStep = ConsentCompletionStep(
      identifier: kConsentSharePdfCompletionStep
    )
    consentCompletionStep.mainTitle = kConsentCompletionMainTitle
    consentCompletionStep.subTitle = kConsentCompletionSubTitle

    // PDF Viewer Step
    let consentViewPdfStep = ConsentPdfViewerStep(identifier: kConsentViewPdfCompletionStep)

    // Final completion step
    let completionStep = CustomCompletionStep(identifier: kConsentCompletionStepIdentifier)
    completionStep.detailText = NSLocalizedString(kConsentCompletionMainTitle, comment: "")

    if (stepArray?.count)! > 0 {
      stepArray?.append(consentCompletionStep)
      stepArray?.append(consentViewPdfStep)
      // stepArray?.append(completionStep)

      let task = ORKNavigableOrderedTask(
        identifier: kConsentTaskIdentifierText,
        steps: stepArray
      )
      return task
    } else {
      return nil
    }
  }
}

// MARK: SharingConsent Struct

struct SharingConsent {
  var shortDesc: String?
  var longDesc: String?
  var learnMore: String?
  var allowWithoutSharing: Bool?
  var text: String?
  var title: String?

  /// Default Initializer
  init() {
    shortDesc = ""
    longDesc = ""
    learnMore = ""
    allowWithoutSharing = false
    text = ""
    title = ""
  }

  /// Initializer method which initializes all params
  /// - Parameter dict: Dictionary which contains all the properties of SharingConsent Step
  public mutating func initWithSharingDict(dict: [String: Any]) {
    if Utilities.isValidObject(someObject: dict as AnyObject?) {
      if Utilities.isValidValue(someObject: dict[kConsentSharingStepShortDesc] as AnyObject) {
        shortDesc = (dict[kConsentSharingStepShortDesc] as? String)!
      }

      if Utilities.isValidValue(someObject: dict[kConsentSharingStepLongDesc] as AnyObject) {
        longDesc = dict[kConsentSharingStepLongDesc] as? String
      }
      if Utilities.isValidValue(someObject: dict[kConsentSharingSteplearnMore] as AnyObject) {
        learnMore = dict[kConsentSharingSteplearnMore] as? String
        if let learnMoreString = learnMore {
          learnMore = learnMoreString.stringByDecodingHTMLEntities
          
          
          let regex = "<[^>]+>"
                      if learnMore?.stringByDecodingHTMLEntities.range(of: regex, options: .regularExpression) == nil {
                          if let valReConversiontoHTMLfromHTML =
                              learnMore?.stringByDecodingHTMLEntities.htmlToAttriString?.attriString2Html {
                              
                              if let attributedText =
                                  valReConversiontoHTMLfromHTML.stringByDecodingHTMLEntities.htmlToAttriString, attributedText.length > 0 {
                                  self.learnMore = attributedText.attriString2Html
                              } else if let attributedText =
                                          learnMoreString.htmlToAttriString?.attriString2Html?.stringByDecodingHTMLEntities.htmlToAttriString,
                                        attributedText.length > 0 {
                                  self.learnMore = attributedText.attriString2Html
                              } else {
                                  self.learnMore = learnMoreString
                              }
                          } else {
                              self.learnMore = learnMoreString
                          }
                      }
          
        }
      }

      if Utilities.isValidValue(someObject: dict[kConsentSharingStepText] as AnyObject) {
        text = dict[kConsentSharingStepText] as? String
      }
      if Utilities.isValidValue(someObject: dict[kConsentSharingStepTitle] as AnyObject) {
        title = dict[kConsentSharingStepTitle] as? String
      }

      allowWithoutSharing = true

    }
  }
}

// MARK: ReviewConsent Struct

struct ReviewConsent {
  var title: String?
  var signatureTitle: String?
  var signatureContent: String?
  var reasonForConsent: String?

  /// Default Initializer
  init() {
    title = ""
    signatureTitle = ""
    signatureContent = ""
    reasonForConsent = ""
  }

  /// Initializer method which initializes all params
  /// - Parameter dict: Dictionary which contains all the properties of ReviewConsent Step
  mutating func initWithReviewDict(dict: [String: Any]) {
    if Utilities.isValidObject(someObject: dict as AnyObject?) {
      if Utilities.isValidValue(someObject: dict[kConsentReviewStepTitle] as AnyObject) {
        title = (dict[kConsentReviewStepTitle] as? String)!
      }

      if Utilities.isValidValue(
        someObject: dict[kConsentReviewStepSignatureTitle] as AnyObject
      ) {
        signatureTitle = dict[kConsentReviewStepSignatureTitle] as? String
      }
      if Utilities.isValidValue(
        someObject: dict[kConsentReviewStepSignatureContent] as AnyObject
      ) {
        signatureContent = dict[kConsentReviewStepSignatureContent] as? String
        if let reviewHTML = signatureContent {
          signatureContent = reviewHTML.stringByDecodingHTMLEntities
          let regex = "<[^>]+>"
                      if signatureContent?.stringByDecodingHTMLEntities.range(of: regex, options: .regularExpression) == nil {
                          if let valReConversiontoHTMLfromHTML =
                              signatureContent?.stringByDecodingHTMLEntities.htmlToAttriString?.attriString2Html {
                              
                              if let attributedText =
                                  valReConversiontoHTMLfromHTML.stringByDecodingHTMLEntities.htmlToAttriString, attributedText.length > 0 {
                                self.signatureContent = attributedText.attriString2Html
                              } else if let attributedText =
                                          reviewHTML.htmlToAttriString?.attriString2Html?.stringByDecodingHTMLEntities.htmlToAttriString,
                                        attributedText.length > 0 {
                                  self.signatureContent = attributedText.attriString2Html
                              } else {
                                  self.signatureContent = reviewHTML
                              }
                          } else {
                              self.signatureContent = reviewHTML
                          }
                      }
        }
      }
      if Utilities.isValidValue(
        someObject: dict[kConsentReviewStepReasonForConsent] as AnyObject
      ) {
        reasonForConsent = dict[kConsentReviewStepReasonForConsent] as? String
      } else {
        reasonForConsent = kMessageconsentConfirmation
      }
    }
  }
}

// MARK: Comprehension Struct

enum Evaluation: String {
  case any
  case all
}

struct Comprehension {
  var passScore: Int?
  var questions: [[String: Any]]?
  var correctAnswers: [[String: Any]]?

  /// Default Initializer
  init() {
    passScore = 0
    questions = []
    correctAnswers = []
  }

  /// Initializer method which initializes all params
  /// - Parameter dict: Dictionary which contains all the properties of Comprehension Step
  mutating func initWithComprehension(dict: [String: Any]) {
    if Utilities.isValidObject(someObject: dict as AnyObject?) {
      if Utilities.isValidValue(someObject: dict[kConsentComprehensionPassScore] as AnyObject) {
        passScore = (dict[kConsentComprehensionPassScore] as? Int)!
      }

      if Utilities.isValidObject(
        someObject: dict[kConsentComprehensionQuestions] as AnyObject
      ) {
        questions = dict[kConsentComprehensionQuestions] as? [[String: Any]]
      }
      if Utilities.isValidObject(
        someObject: dict[kConsentComprehensionCorrectAnswers] as AnyObject
      ) {
        correctAnswers = dict[kConsentComprehensionCorrectAnswers] as? [[String: Any]]
      }
    }
  }
}

// MARK: Custom Clases

class ConsentReviewStep: ORKConsentReviewStep {

}

class VisualConsentStep: ORKVisualConsentStep {

}

class ConsentSharingStep: ORKConsentSharingStep {

}

class CustomCompletionStep: ORKCompletionStep {
  func showsProgress() -> Bool {
    return true
  }
}

