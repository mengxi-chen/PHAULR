package test.ui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import storage.Replica;
import test.ConsProBenchmarkRunner;
import util.FileUtil;

import communication.Address;
import communication.AddressPlusRid;
import communication.Configuration;

public class ConsProApp extends JFrame implements ActionListener, ItemListener
{
	public static String APP = "ConsProApp";

	public static String REPLICA_BTN = "Replica";
	public static String CLIENT_BTN = "Client";

	public static String REPLICA_BTN_ACTION = "StartAsReplica";
	public static String CLIENT_BTN_ACTION = "StartAsClient";

	public static String START_BTN = "Start";
	public static String START_BTN_ACTION = "StartAction";

	public static String BROADCAST_BTN = "Broadcast";
	public static String BROADCAST_BTN_ACTION = "BroadcastAction";

	private static final long serialVersionUID = -7775787854246869928L;

	private JPanel contentPane;

	private ButtonGroup role_grp;
	private JRadioButton replica_radio_btn;
	private JRadioButton client_radio_btn;

	private JComboBox<AddressPlusRid> replica_combo_box;
	private JTextField client_addr_txt;
	private JComboBox<Address> default_replica_combo_box;

	private JButton start_btn;
	private JButton broadcast_btn;

	private JTextArea console_txtarea;
//	private JScrollPane console_pane;

	/**
	 * Create the frame.
	 */
	public ConsProApp()
	{
		setTitle(ConsProApp.APP);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 600);
		setPreferredSize(new Dimension(450, 600));

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		/**
		 * ButtonGroup for choosing the role to run: replica or client
		 */
		replica_radio_btn = new JRadioButton(ConsProApp.REPLICA_BTN);
		replica_radio_btn.setActionCommand(ConsProApp.REPLICA_BTN_ACTION);
		replica_radio_btn.setSelected(true);
		replica_radio_btn.addItemListener(this);

		client_radio_btn = new JRadioButton(ConsProApp.CLIENT_BTN);
		client_radio_btn.setActionCommand(ConsProApp.CLIENT_BTN_ACTION);
		client_radio_btn.addItemListener(this);

		role_grp = new ButtonGroup();
		role_grp.add(replica_radio_btn);
		role_grp.add(client_radio_btn);

		/**
		 * Choose replica
		 */
		Configuration.INSTANCE.configSystem();

		Address[] replica_pool = Configuration.INSTANCE.getReplicaPool();
		replica_combo_box = new JComboBox<AddressPlusRid>(AddressPlusRid.attachRids(replica_pool));
		replica_combo_box.setToolTipText("Choose the address and id of this replica.");

		/**
		 * Broadcast button for replica
		 */
		broadcast_btn = new JButton(ConsProApp.BROADCAST_BTN);
		broadcast_btn.setActionCommand(ConsProApp.BROADCAST_BTN_ACTION);
		broadcast_btn.addActionListener(this);
		broadcast_btn.setEnabled(false);

		/**
		 * Input client address
		 */
		client_addr_txt = new JTextField();
		client_addr_txt.setEnabled(false);
		client_addr_txt.setText("127.0.0.1 : 21000");
		client_addr_txt.setColumns(15);

		/**
		 * Choose default replica to contact
		 */
		default_replica_combo_box = new JComboBox<Address>(Configuration.INSTANCE.getReplicaPool());
		default_replica_combo_box.setEnabled(false);

		/**
		 * Start button
		 */
		start_btn = new JButton(ConsProApp.START_BTN);
		start_btn.setActionCommand(ConsProApp.START_BTN_ACTION);
		start_btn.addActionListener(this);

//		/**
//		 * Redirect the system console output to the JTextArea GUI.
//		 */
//		console_txtarea = new JTextArea("----- Console here ----- \n", Integer.MAX_VALUE, Integer.MAX_VALUE);
//		console_txtarea.setPreferredSize(new Dimension(200, 300));
//        TextAreaOutputStream taos = new TextAreaOutputStream(console_txtarea, Integer.MAX_VALUE);
//        PrintStream ps = new PrintStream(taos);
//        System.setOut(ps);
//        System.setErr(ps);
//        console_pane = new JScrollPane(console_txtarea);

		/**
		 * Adding to this panel in group layout
		 */
        this.fillInGroupLayout();

        this.pack();
        this.setResizable(false);
	}

	private void fillInGroupLayout()
	{
		GroupLayout group_layout = new GroupLayout(contentPane);

		group_layout.setHorizontalGroup(
				group_layout.createParallelGroup(Alignment.LEADING)
					.addGroup(group_layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(group_layout.createParallelGroup(Alignment.TRAILING)
							.addGroup(group_layout.createSequentialGroup()
								.addGroup(group_layout.createParallelGroup(Alignment.LEADING)
									.addComponent(replica_radio_btn)
									.addComponent(client_radio_btn))
								.addGap(37)
								.addGroup(group_layout.createParallelGroup(Alignment.LEADING)
									.addComponent(replica_combo_box, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(client_addr_txt, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(ComponentPlacement.RELATED, 121, Short.MAX_VALUE)
								.addGroup(group_layout.createParallelGroup(Alignment.TRAILING)
									.addComponent(broadcast_btn)
									.addComponent(default_replica_combo_box, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
//							.addComponent(console_pane, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
							)
						.addContainerGap(24, Short.MAX_VALUE))
					.addGroup(group_layout.createSequentialGroup()
						.addGap(177)
						.addComponent(start_btn)
						.addContainerGap(184, Short.MAX_VALUE))
			);

			group_layout.setVerticalGroup(
				group_layout.createParallelGroup(Alignment.LEADING)
					.addGroup(group_layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(group_layout.createParallelGroup(Alignment.BASELINE)
							.addComponent(replica_radio_btn)
							.addComponent(replica_combo_box, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(broadcast_btn))
						.addGap(18)
						.addGroup(group_layout.createParallelGroup(Alignment.BASELINE)
							.addComponent(client_radio_btn)
							.addComponent(client_addr_txt, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(default_replica_combo_box, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGap(18)
						.addComponent(start_btn)
						.addGap(18)
//						.addComponent(console_pane, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(41, Short.MAX_VALUE))
			);

		contentPane.setLayout(group_layout);
		group_layout.setAutoCreateGaps(true);
		group_layout.setAutoCreateContainerGaps(true);
	}

	/**
	 * Start working (as a replica or a client).
	 * If it acts a replica, it should also start the broadcast service.
	 */
	@Override
	public void actionPerformed(ActionEvent action_event)
	{
		String action = action_event.getActionCommand();

		// for log4j configuration
//		RollingFileAppender appender = new RoleSpecificRollingFileAppender();

		if (action.equals(ConsProApp.START_BTN_ACTION))
		{
			String role = this.role_grp.getSelection().getActionCommand();

			// act as a replica
			if (role.equals(ConsProApp.REPLICA_BTN_ACTION))
			{
				// for log4j configuration
//				appender.setFile(Configuration.REPLICA_LOG_FILE);
//				BasicConfigurator.configure(appender);

				/**
				 * Write the chosen replica address (and its id) into the Configuration.CONFIG_REPLICA_FILE file
				 */
				String replica_addr_rid = ((AddressPlusRid) this.replica_combo_box.getSelectedItem()).toString();
				FileUtil.write2File(Configuration.CONFIG_REPLICA_FILE, replica_addr_rid);


				Replica.INSTANCE.start();
				this.broadcast_btn.setEnabled(true);

			}
			// act as a client
			else // role.equals(ConsProApp.CLIENT_BTN_ACTION)
			{
				// for log4j configuration
//				appender.setFile(Configuration.CLIENT_LOG_FILE);
//				BasicConfigurator.configure(appender);

				/**
				 * Write the user-input client address and the chosen default replica address into
				 * the Configuration.CONFIG_CLIENT_FILE file.
				 */

				String client_addr = this.client_addr_txt.getText().trim();
				String default_replica_addr = ((Address) this.default_replica_combo_box.getSelectedItem()).toString();

				StringBuilder client_config = new StringBuilder();
				client_config.append(client_addr).append("\n").append(default_replica_addr);
				FileUtil.write2File(Configuration.CONFIG_CLIENT_FILE, client_config.toString());

				// run the benchmark
				new ConsProBenchmarkRunner(10000, 20).start();
			}

			this.replica_radio_btn.setEnabled(false);
			this.client_radio_btn.setEnabled(false);
			this.replica_combo_box.setEnabled(false);
			this.client_addr_txt.setEnabled(false);
			this.default_replica_combo_box.setEnabled(false);
			this.start_btn.setEnabled(false);
		}
		else if (action.equals(ConsProApp.BROADCAST_BTN_ACTION))
		{
			Replica.INSTANCE.startBroadcastService();
			this.broadcast_btn.setEnabled(false);
		}
	}

	/**
	 * Identify the role as a replica or a client
	 */
	@Override
	public void itemStateChanged(ItemEvent item_event)
	{
		if (item_event.getStateChange() == ItemEvent.SELECTED)
		{
			String role = this.role_grp.getSelection().getActionCommand();

			if (role.equals(ConsProApp.REPLICA_BTN_ACTION))
			{
				this.replica_combo_box.setEnabled(true);

				this.client_addr_txt.setEnabled(false);
				this.default_replica_combo_box.setEnabled(false);
			}
			else // role.equals(ConsProApp.CLIENT_BTN_ACTION)
			{
				this.client_addr_txt.setEnabled(true);
				this.default_replica_combo_box.setEnabled(true);

				this.replica_combo_box.setEnabled(false);
			}
		}
	}

	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					ConsProApp frame = new ConsProApp();
					frame.setVisible(true);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

}

