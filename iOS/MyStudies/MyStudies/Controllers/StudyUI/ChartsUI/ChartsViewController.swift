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

import UIKit
import FirebaseAnalytics

let kMessageForSharingCharts =
  "This action will create a shareable image file of the charts currently seen in this section. Proceed?"

class ChartsViewController: UIViewController {

  // MARK: - Outlets
  @IBOutlet weak var tableView: UITableView!

  @IBOutlet weak var backButton: UIButton!

  // MARK: - LifeCycle
  override func viewDidLoad() {
    super.viewDidLoad()
    self.title = NSLocalizedString("Trends", comment: "")
  }

  override func viewWillAppear(_ animated: Bool) {
    super.viewWillAppear(animated)

    DispatchQueue.main.async {
      guard let studyId = Study.currentStudy?.studyId else { return }
      DBHandler.loadChartsForStudy(studyId: studyId) { (chartList) in
        if chartList.count != 0 {
          StudyDashboard.instance.charts = chartList
          self.tableView?.reloadData()
        }
      }
    }
  }

  // MARK: - Actions
  @IBAction func backButtonAction(_ sender: UIButton) {
    Analytics.logEvent(analyticsButtonClickEventsName, parameters: [
      buttonClickReasonsKey: "Back Button"
    ])
    self.navigationController?.popViewController(animated: true)
  }

  @IBAction func shareButtonAction(_ sender: AnyObject) {
    Analytics.logEvent(analyticsButtonClickEventsName, parameters: [
      buttonClickReasonsKey: "Charts Share"
    ])

    if StudyDashboard.instance.charts.count > 0 {

      UIUtilities.showAlertMessageWithTwoActionsAndHandler(
        NSLocalizedString(kTitleMessage, comment: ""),
        errorMessage: NSLocalizedString(kMessageForSharingCharts, comment: ""),
        errorAlertActionTitle: NSLocalizedString(kTitleOK, comment: ""),
        errorAlertActionTitle2: NSLocalizedString(kTitleCancel, comment: ""),
        viewControllerUsed: self,
        action1: {

          self.shareScreenShotByMail()
        },

        action2: {
          Analytics.logEvent(analyticsButtonClickEventsName, parameters: [
            buttonClickReasonsKey: "Charts Cancel"
          ])
          // Handle cancel action.
        }
      )

    }

  }

  /// To Create screen shot of current visible view.
  func shareScreenShotByMail() {
    //Create the UIImage
    let image = tableView.asFullImage()
    
    (self.tabBarController as! StudyDashboardTabbarViewController).shareScreenshotByEmail(
      image: image,
      subject: kEmailSubjectCharts,
      fileName: kEmailSubjectCharts
    )

  }

}

extension ChartsViewController: UITableViewDataSource {

  func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
    return StudyDashboard.instance.charts.count
  }

  func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {

    let chart = StudyDashboard.instance.charts[indexPath.row]

    if chart.chartType == "line-chart",
      let cell = tableView.dequeueReusableCell(withIdentifier: "lineChart", for: indexPath) as? LineChartCell
    {
      cell.setupLineChart(chart: chart)
      return cell
    } else {
      let cell = UITableViewCell()
      return cell
    }
  }

}
