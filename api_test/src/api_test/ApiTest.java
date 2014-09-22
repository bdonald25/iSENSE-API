package api_test;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFrame;

import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uml.cs.isense.api.API;
import edu.uml.cs.isense.api.UploadInfo;
import edu.uml.cs.isense.objects.RPerson;
import edu.uml.cs.isense.objects.RProject;
import edu.uml.cs.isense.objects.RProjectField;

public class ApiTest {
	private JFrame frame;
	
	int FILEPICK = 0;
	int MEDIAPROJPICK = 1;
	int MEDIADATASET = 2;
	
	private JButton dev;
	private JButton live;
	
	API api;
	
	int projectId;
	JPanel buttons;
	JPanel results;
	
	UploadInfo dataSetInfo;
	
	ArrayList<RProjectField> fields;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ApiTest window = new ApiTest();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Constructor for APITest window
	 */
	public ApiTest() {
		initialize();
	}

	/**
	 * Initialize the contents of the JFrame (The window).
	 */
	private void initialize() {
		//initialize api object
		api = API.getInstance();

		//text for status bar
		frame = new JFrame("API Tests");
		
		//get window size
		Toolkit tk = Toolkit.getDefaultToolkit();  
		int xSize = ((int) tk.getScreenSize().getWidth());  
		int ySize = ((int) tk.getScreenSize().getHeight());
		frame.setSize(xSize, ySize); 
		
		//what happens when user closes window
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Set up window layout as grid of 2 rows and 1 column
		BoxLayout myLayout = new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS);
		frame.setLayout(myLayout);
		
		//buttons
		dev = new JButton("Test Dev");
		live = new JButton("Test Production");
		
		//Size of buttons
		dev.setSize(100,50);
		live.setSize(100,50);

		//JLabel status = new JLabel("Results will be displayed here.");

		//adding panels to main window
		buttons = new JPanel();
		buttons.add(dev);
		buttons.add(live);	
		buttons.setMinimumSize(new Dimension(300, 50));
		buttons.setMaximumSize(new Dimension(300, 50));


		//panel to show results of test
		results = new JPanel();
		results.setLayout(new BoxLayout(results, BoxLayout.Y_AXIS));
		
		JScrollPane scrollPane = new JScrollPane(results);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		
		//add button panel and results panel to window
		frame.add(buttons);
		frame.add(scrollPane);
		frame.revalidate();
		
		
		//Called dev button is clicked
		dev.addActionListener(new ActionListener() {
			@Override
				public void actionPerformed(ActionEvent e) {
					api.useDev(true);
					JLabel status = new JLabel();
					status.setText("Starting tests on rsense-dev...");
					Font font = status.getFont();
					Font bold = new Font(font.getFontName(), Font.BOLD, font.getSize());
					status.setFont(bold);
					status.setAlignmentX(Component.CENTER_ALIGNMENT);
				
					results.add(status);
					
					frame.revalidate();
					new LoginTask().execute();
				}
	        });      
				
		
		//Called when live button is clicked
		live.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				api.useDev(false);
				JLabel status = new JLabel();
				status.setText("Starting tests on isenseproject...");
				Font font = status.getFont();
				Font bold = new Font(font.getFontName(), Font.BOLD, font.getSize());
				status.setFont(bold);
				status.setAlignmentX(Component.CENTER_ALIGNMENT);
				results.add(status);
				
				frame.revalidate();
				new LoginTask().execute();
			}
        });
	}
	
	/**
	 * Tests logging in. Logs in with email- mobile.fake@example.com password- mobile
	 * @author bobby
	 *
	 */
	 class LoginTask extends SwingWorker<Object, Object> {
        /**
         * @throws Exception
         */
        protected Object doInBackground() throws Exception {				
        	RPerson person = api.createSession("mobile.fake@example.com", "mobile");

	    	boolean success = (person != null);
	    	System.out.print(success);
	    	
        	SwingUtilities.invokeLater(new Runnable() {
        	    public void run() {
					JLabel status = new JLabel();
					
    				if(success) {
    					status.setText("\nLogin successful.");
						status.setAlignmentX(Component.CENTER_ALIGNMENT);
						status.setForeground(Color.green);
						results.add(status);
						frame.revalidate();
    				} else {
    					status.setText("\nLogin failed.");
						status.setAlignmentX(Component.CENTER_ALIGNMENT);
						status.setForeground(Color.red);
						results.add(status);
						frame.revalidate();
    				}
					new CreateProjectTask().execute();
        	    }
        	  });
			return success;
	    }
	 }
	        
	 /**
	 *  Test Creating a Project 
	 * @author bobby
	 *
	 */
	 class CreateProjectTask extends SwingWorker<Object, Object> {
	        /**
	         * @throws Exception
	         */
	        protected Object doInBackground() throws Exception {
				ArrayList<RProjectField> fields = new ArrayList<RProjectField>();
				
				RProjectField time = new RProjectField();
				time.type = RProjectField.TYPE_TIMESTAMP;
				time.name = "Time";
				fields.add(time);
				
				RProjectField amount = new RProjectField();
				amount.type = RProjectField.TYPE_NUMBER;
				amount.name = "Amount";
				amount.unit = "units";
				fields.add(amount);
				
				projectId = api.createProject("Test Project", fields);
				JLabel status = new JLabel();

				if(projectId == -1) {
					status.setText("Create project fail.");
					status.setAlignmentX(Component.CENTER_ALIGNMENT);
					status.setForeground(Color.red);

					results.add(status);
					frame.revalidate();
				} else {
					status.setText("Create project Success.");
					status.setAlignmentX(Component.CENTER_ALIGNMENT);
					status.setForeground(Color.green);

					results.add(status);
					frame.revalidate();
				}
				new ProjectsTask().execute();
				
				return null;

	        }
  		}
       
       /**
        *  Tests api call to get a list of the projects on isense
        * @author bobby
        *
        */
        class ProjectsTask extends SwingWorker<Object, Object> {
	        /**
	         * @throws Exception
	         */
	        protected Object doInBackground() throws Exception {
	    		ArrayList<ArrayList<RProjectField>> rpfs = new ArrayList<ArrayList<RProjectField>>();
	        	ArrayList<RProject> rps = api.getProjects(1, 2, true, API.CREATED_AT, "");
	        	
				for(RProject rp : rps) {
					rpfs.add(api.getProjectFields(rp.project_id));
				}	
				
				JLabel projects = new JLabel();
				projects.setText("Attempting to get 2 projects and their fields");
				projects.setAlignmentX(Component.CENTER_ALIGNMENT);
				results.add(projects);
				frame.revalidate();
				
				for(RProject p : rps) {
					JLabel project = new JLabel();
					project.setText("Project name: " + p.name);
					project.setAlignmentX(Component.CENTER_ALIGNMENT);

					results.add(project);
					results.revalidate();

					if(rpfs.size() > 0) {
						for(RProjectField rp : rpfs.remove(0)) {
							JLabel field = new JLabel();
							field.setText("Field name: " + rp.name);
							field.setAlignmentX(Component.CENTER_ALIGNMENT);
							results.add(field);
							results.revalidate();
						}
					}
				}
			
				if(rps.size() <= 2) {
					JLabel status = new JLabel();
					status.setText("Get (at most) 2 Projects successful");
					status.setAlignmentX(Component.CENTER_ALIGNMENT);
					status.setForeground(Color.green);

					results.add(status);
					frame.revalidate();
				} else {
					JLabel status = new JLabel();
					status.setText("Get (at most) 2 Projects failed");
					status.setAlignmentX(Component.CENTER_ALIGNMENT);
					status.setForeground(Color.red);

					results.add(status);
					frame.revalidate();
				}
				
				new GetFieldIdsTask().execute();
				
				return null;
	        }
        }	
  
	 	/**
	 	 * Tests api call to get fields for a specific project
	 	 * @author bobby
	 	 *
	 	 */
	 	class GetFieldIdsTask extends SwingWorker<Object, Object> {
	        /**
	         * @throws Exception
	         */
	        protected Object doInBackground() throws Exception {
	        	fields = api.getProjectFields(projectId);
				new UploadTask().execute();
				return null;
		    }
	 	}
	    
	 	/**
	 	 * Tests uploading data to a project
	 	 * @author bobby
	 	 *
	 	 */
     class UploadTask extends SwingWorker<Object, Object> {
	        /**
	         * @throws Exception
	         */
	        protected Object doInBackground() throws Exception {
	        	JSONObject j = new JSONObject();
	        	String field1 = Long.toString(fields.get(0).field_id);
	        	String field2 = Long.toString(fields.get(1).field_id);
				try {
					j.put(field1, new JSONArray().put("2013/08/02 09:50:01"));
					j.put(field2, new JSONArray().put("45"));
				} catch (JSONException e) {
					e.printStackTrace();
					UploadInfo info = new UploadInfo();
					return info;
				}
				
				dataSetInfo = api.uploadDataSet(projectId, j, "mobile upload test");
				
				JLabel status = new JLabel();
				if(dataSetInfo.dataSetId == -1) {
					status.setText("Upload data set fail.");
					status.setAlignmentX(Component.CENTER_ALIGNMENT);
					status.setForeground(Color.red);

					results.add(status);
					frame.revalidate();
				} else {
					status.setText("Upload data set success.");
					status.setAlignmentX(Component.CENTER_ALIGNMENT);
					status.setForeground(Color.green);

					results.add(status);
					frame.revalidate();
				}
				new AppendTask().execute();
	        	return null;
		    }
     }
     
     /**
      * Tests appending to a dataset
      * @author bobby
      *
      */
     class AppendTask extends SwingWorker<Object, Object> {
	        /**
	         * @throws Exception
	         */
	        protected Object doInBackground() throws Exception {
	        	JSONObject toAppend = new JSONObject();
	        	
	        	String field1 = Long.toString(fields.get(0).field_id);
	        	String field2 = Long.toString(fields.get(1).field_id);

				try {
					toAppend.put(field1, new JSONArray().put("2013/08/05 10:50:20"));
					toAppend.put(field2, new JSONArray().put("119"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				boolean success = api.appendDataSetData(dataSetInfo.dataSetId, toAppend);
				
				JLabel status = new JLabel();
				if(success) {
					status.setText("Append data set success.");
					status.setAlignmentX(Component.CENTER_ALIGNMENT);
					status.setForeground(Color.green);

					results.add(status);
					frame.revalidate();
				} else {
					status.setText("Append data set fail.");
					status.setAlignmentX(Component.CENTER_ALIGNMENT);
					status.setForeground(Color.red);

					results.add(status);
					frame.revalidate();
				} 
				
				new ProjMediaTask().execute();
				
		        return null;
		    }
  }
     /**
      * Tests uploading media
      * @author bobby
      *
      */
     class ProjMediaTask extends SwingWorker<Object, Object> {
	        /**
	         * @throws Exception
	         */
	        protected Object doInBackground() throws Exception {
				UploadInfo info = api.uploadMedia(projectId, new File("test.jpg"), API.TargetType.PROJECT);
				
				
				JLabel status = new JLabel();
				if(info.mediaId != -1) {
					status.setText("Upload media to project success.");
					status.setAlignmentX(Component.CENTER_ALIGNMENT);
					status.setForeground(Color.green);

					results.add(status);
					frame.revalidate();
				} else {
					status.setText("Upload media to project fail.");
					status.setAlignmentX(Component.CENTER_ALIGNMENT);
					status.setForeground(Color.red);

					results.add(status);
					frame.revalidate();
				} 
				

				new DSMediaTask().execute();
				
				return null;
		    }
     }
	/**
	 * Tests uploading media to a data set
	 * @author bobby
	 *
	 */
     class DSMediaTask extends SwingWorker<Object, Object> {
	        /**
	         * @throws Exception
	         */
	        protected Object doInBackground() throws Exception {
				UploadInfo info = api.uploadMedia(dataSetInfo.dataSetId, new File("test.jpg"), API.TargetType.DATA_SET);
				
				JLabel status = new JLabel();
				if(info.mediaId != -1) {
					status.setText("Upload media to data set successful.");
					status.setAlignmentX(Component.CENTER_ALIGNMENT);
					status.setForeground(Color.green);

					results.add(status);
					frame.revalidate();
				} else {
					status.setText("Upload media to data set failed.");
					status.setAlignmentX(Component.CENTER_ALIGNMENT);
					status.setForeground(Color.red);

					results.add(status);
					frame.revalidate();
				} 
				

	        	new LogoutTask().execute();
				return null;
		    }
     }
	
     //TODO Delete Project api call on website does not exist as of right now 9/18/14
     /**
      * Tests Deleting a project
      * @author bobby
      *
      */
	 class DeleteProjectTask extends SwingWorker<Object, Object> {
        /**
         * @throws Exception
         */
        protected Object doInBackground() throws Exception {

        	int success = api.deleteProject(projectId);
			JLabel status = new JLabel();

			if(success == 1) {
				status.setText("Delete project sucess.");
				status.setAlignmentX(Component.CENTER_ALIGNMENT);
				status.setForeground(Color.green);

				results.add(status);
				frame.revalidate();
			} else {
				status.setText("Delete project fail.");
				status.setAlignmentX(Component.CENTER_ALIGNMENT);
				status.setForeground(Color.red);

				results.add(status);
				frame.revalidate();
			}
			
			new LogoutTask().execute();
			
			return null;
	    }
	}
	 
	 /**
	 * Tests logging out
	 * @author bobby
	 *
	 */
     class LogoutTask extends SwingWorker<Object, Object> {
	        /**
	         * @throws Exception
	         */
	        protected Object doInBackground() throws Exception {
				api.deleteSession();
				
				JLabel status = new JLabel();
				Font font = status.getFont();
				Font bold = new Font(font.getFontName(), Font.BOLD, font.getSize());
				status.setFont(bold);
				status.setText("Testing Complete.");
				status.setAlignmentX(Component.CENTER_ALIGNMENT);
				results.add(status);
				frame.revalidate();
				
				return null;
		    }
     }
		
}