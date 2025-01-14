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

import IQKeyboardManagerSwift
import ResearchKit
import UIKit
import FirebaseAnalytics

let kTitleOK = "OK"

class EligibilityStep: ORKStep {
  var type: String?

  func showsProgress() -> Bool {
    return false
  }
}

// MARK: ORKResult overriding
open class EligibilityTokenTaskResult: ORKResult {
  open var enrollmentToken: String = ""

  override open var description: String {
    return "enrollmentToken:\(enrollmentToken)"
  }

  override open var debugDescription: String {
    return "enrollmentToken:\(enrollmentToken)"
  }
}

class EligibilityStepViewController: ORKStepViewController {

  // MARK: - Outlets
  @IBOutlet weak var tokenTextField: UITextField!

  @IBOutlet weak var buttonSubmit: UIButton?
  @IBOutlet weak var labelDescription: UILabel?

  // MARK: - UI Properties
  var descriptionText: String?

  /// The max characters allowed for user to enter token.
  let maxCharForToken = 8

  var taskResult: EligibilityTokenTaskResult = EligibilityTokenTaskResult(
    identifier: kFetalKickCounterStepDefaultIdentifier
  )

  // MARK: - ORKStepViewController Intitialization Methods

  override init(step: ORKStep?) {
    super.init(step: step)
  }

  required init?(coder aDecoder: NSCoder) {
    super.init(coder: aDecoder)
  }

  override func hasNextStep() -> Bool {
    super.hasNextStep()
    return true
  }

  override func goForward() {
    NotificationCenter.default.post(name: Notification.Name("GoForward"), object: nil)
    super.goForward()
  }

  override var result: ORKStepResult? {

    let orkResult = super.result
    orkResult?.results = [self.taskResult]
    return orkResult

  }

  // MARK: - LifeCycle
  override func viewDidLoad() {
    super.viewDidLoad()

    buttonSubmit?.layer.borderColor = kUicolorForButtonBackground

    if (self.descriptionText?.count)! > 0 {
      labelDescription?.text = self.descriptionText
    }

    if let step = step as? EligibilityStep {
      step.type = "token"
    }
    tokenTextField.delegate = self
    IQKeyboardManager.shared.disabledDistanceHandlingClasses.append(EligibilityStepViewController.self)

  }

  override func viewWillAppear(_ animated: Bool) {
    super.viewWillAppear(animated)

    let footerView = ORKNavigationContainerView()
    footerView.translatesAutoresizingMaskIntoConstraints = false
    footerView.neverHasContinueButton = true
    footerView.cancelButtonItem = self.cancelButtonItem
    footerView.skipEnabled = false
    self.view.addSubview(footerView)

    print("UIScreen.main.bounds.height---\(UIScreen.main.bounds.height)")
    let valHeight = (UIScreen.main.bounds.height * 70) / 870
    NSLayoutConstraint.activate(
      [
        footerView.bottomAnchor.constraint(equalTo: self.view.bottomAnchor, constant: 0),
        footerView.heightAnchor.constraint(equalToConstant: valHeight),
        footerView.leadingAnchor.constraint(equalTo: self.view.leadingAnchor, constant: 0),
        footerView.trailingAnchor.constraint(
          equalTo: self.view.trailingAnchor,
          constant: 0
        ),
      ])
  }

  // MARK: - UI Utils

  func showAlert(message: String) {
    let alert = UIAlertController(
      title: kErrorTitle as String,
      message: message as String,
      preferredStyle: UIAlertController.Style.alert
    )
    alert.addAction(
      UIAlertAction(
        title: NSLocalizedString(kTitleOK, comment: ""),
        style: .default,
        handler: nil
      )
    )
    self.present(alert, animated: true, completion: nil)
  }
  
  /// For lifting the view up
  func animateViewMoving (up:Bool, moveValue :CGFloat) {
    let movementDuration:TimeInterval = 0.3
    let movement:CGFloat = ( up ? -moveValue : moveValue)
    UIView.beginAnimations( "animateView", context: nil)
    UIView.setAnimationBeginsFromCurrentState(true)
    UIView.setAnimationDuration(movementDuration )
    self.view.frame = self.view.frame.offsetBy(dx: 0, dy: movement)
    UIView.commitAnimations()
  }

  // MARK: - Action
  @IBAction func buttonActionSubmit(sender: UIButton?) {
    Analytics.logEvent(analyticsButtonClickEventsName, parameters: [
      buttonClickReasonsKey: "EligibilityStep Submit"
    ])

    self.view.endEditing(true)
    let token = tokenTextField.text

    if (token?.isEmpty) == false {
      guard let studyId = Study.currentStudy?.studyId else { return }
      EnrollServices().verifyEnrollmentToken(
        studyId: studyId,
        token: token!,
        delegate: self
      )
    } else {
      self.showAlert(title: kTitleMessage, message: kMessageValidToken)
    }
  }

}

// MARK: TextField Delegates
extension EligibilityStepViewController: UITextFieldDelegate {

  func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool {
    return true
  }

  func textField(
    _ textField: UITextField,
    shouldChangeCharactersIn range: NSRange,
    replacementString string: String
  ) -> Bool {

    if string == " " {
      return false  // Ignore spaces
    } else if let text = textField.text,
      (text + string).count > maxCharForToken
    {
      return false  // Block user typing more than max characters allowed.
    } else {
      return true
    }
  }
  
  func textFieldDidBeginEditing(_ textField: UITextField) {
    animateViewMoving(up: true, moveValue: 100)
  }

  func textFieldDidEndEditing(_ textField: UITextField) {
    animateViewMoving(up: false, moveValue: 100)
  }
}

// MARK: Webservice Delegates
extension EligibilityStepViewController: NMWebServiceDelegate {

  func startedRequest(_ manager: NetworkManager, requestName: NSString) {
    self.addProgressIndicator()
  }

  func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {
    self.removeProgressIndicator()

    if (tokenTextField.text?.isEmpty) == false {
      self.taskResult.enrollmentToken = tokenTextField.text!
      // Storing token so that it can be used in case of ineligibility
      let appdelegate = UIApplication.shared.delegate as! AppDelegate
      appdelegate.consentToken = tokenTextField.text!

    } else {
      self.taskResult.enrollmentToken = ""
    }
    NotificationCenter.default.post(name: Notification.Name("GoForward"), object: nil)
    self.goForward()
  }

  func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {
    self.removeProgressIndicator()
    self.showAlert(message: error.localizedDescription)
  }
}
