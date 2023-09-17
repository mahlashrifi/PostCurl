package ui.gui;

import logic.Core;
import logic.Request;
import logic.ResponseInfo;
import org.apache.http.client.methods.CloseableHttpResponse;
import ui.console.ConsoleView;
import javax.swing.*;
import java.awt.*;
import java.util.Date;

/**
 * It is class which extends swing worker to fill response panel according to data
 */
    public class GuiResponseMaker extends SwingWorker<JPanel,Double> {
    private Core core ;

    private Request request ;
    private ResponseInfo responseInfo ;

    private JPanel responsePanel ;
    private ResponsePanel response ;
    private JButton cancelButton ;
    private JLabel time ;
    private JPanel nothing ;


    private Date startTime ;


    public GuiResponseMaker(Request request , JPanel responsePanel , ResponsePanel response ,JPanel nothing, JButton cancelButton , JLabel time) throws Exception {
        core = Core.getInstance();
        this.request = request;
        this.responsePanel = responsePanel;
        this.response = response;
        this.cancelButton = cancelButton;
        this.time = time;
        this.nothing = nothing;

    }

    @Override
    protected JPanel doInBackground() throws Exception{
                  try {
                      startTime = new Date();

                      ((CardLayout) responsePanel.getLayout()).show(responsePanel , "WaitingPanel");
                      time.setVisible(true);
                      cancelButton.setVisible(true);
                      cancelButton.addActionListener(e -> this.cancel(true));
                      Double finalTimeInSecond;
                      CloseableHttpResponse httpResponse;
                      boolean followRedirect = MenuBar.getInstance().isFollowRedirect();
                      while (true) {
                          httpResponse = core.getResponseOfRequest(request);
                          if ((httpResponse.getStatusLine().getStatusCode() / 100 == 3) && followRedirect)
                              request.setUrl(ConsoleView.findNewLocation(httpResponse.getAllHeaders()));
                          else
                              break;

                      }
                      responseInfo = new ResponseInfo(httpResponse);
                      Date secondaryDate = new Date();
                      finalTimeInSecond = (secondaryDate.getTime() - startTime.getTime()) / 1000.0;
                      responseInfo.setTimeOfSend(finalTimeInSecond + " s");

                      ((CardLayout) responsePanel.getLayout()).show(responsePanel , "Response");
                      response.fillResponsePanel(responseInfo);
                      System.err.println("helllo");
                      return responsePanel;
                  }
                  catch (Exception e){
                     this.cancel(true);
                      JLabel label = new JLabel(e.getMessage());
                      label.setForeground(Color.RED);
                      label.setFont(new Font("Verdana", Font.PLAIN, 12));
                      nothing.add(label,BorderLayout.NORTH);
                      ((CardLayout) responsePanel.getLayout()).show(responsePanel,"Nothing");
                      throw e;

                  }


    }

    protected void doProcess(){
        Double timeInSecond;
        Date secondaryDate = new Date();
        timeInSecond = (secondaryDate.getTime() - startTime.getTime())/1000.0;
        time.setText(timeInSecond+" s");
    }

    protected void done()
    {


    }

}
