package org.swdc.note.app.ui.view.quick;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.swing.*;


import com.sun.awt.AWTUtilities;
import de.felixroske.jfxsupport.GUIState;
import javafx.application.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.swdc.note.app.event.ViewChangeEvent;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.ui.view.StartEditView;
import org.swdc.note.app.util.UIUtil;

@Component
public class QuickFrame extends JWindow {

	private static final long serialVersionUID = 1L;
	private int prefX,prefY;
	private boolean flg;

	@Autowired
	private UIConfig config;

	@Autowired
	private StartEditView editView;

	/**
	 * Create the frame.
	 */
	public QuickFrame() {
		JPopupMenu menu = new JPopupMenu();
		JMenuItem itemStage = new JMenuItem("打开主窗口");
		JMenuItem itemAdd = new JMenuItem("创建文档");
		JMenuItem itemExit = new JMenuItem("退出");

		itemStage.addActionListener(e->{
			Platform.runLater(()->{
				if(GUIState.getStage().isShowing()){
					GUIState.getStage().requestFocus();
				}else{
					GUIState.getStage().show();
				}
			});
		});

		itemAdd.addActionListener(e->
			Platform.runLater(()->
				Platform.runLater(()->{
					if(UIUtil.isClassical()){
						if(editView.getStage().isShowing()){
							editView.getStage().requestFocus();
						}else{
							editView.getStage().show();
						}
					}else{
						if(GUIState.getStage().isShowing()){
							GUIState.getStage().requestFocus();
						}else{
							GUIState.getStage().show();
						}
						config.publishEvent(new ViewChangeEvent("EditView"));
					}
				})));

		itemExit.addActionListener(e->{
			Platform.runLater(()->{
				Platform.exit();
				System.exit(0);
			});
		});

		menu.add(itemStage);
		menu.add(itemAdd);
		menu.add(itemExit);

		AWTUtilities.setWindowOpaque(this, false);
		setBounds(1000, 60, 100, 100);

		addMouseListener(new MouseAdapter(){

			public void mousePressed(MouseEvent e) {
				requestFocus();
				flg = true;
				prefX = e.getX();
				prefY = e.getY();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				flg = false;
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON3){
					menu.setInvoker(QuickFrame.this);
					menu.setLocation(e.getXOnScreen(),e.getYOnScreen());
					menu.setVisible(true);
				} else if (e.getClickCount() >= 2){
					Platform.runLater(()->{
						if(GUIState.getStage().isShowing()){
							GUIState.getStage().requestFocus();
						}else{
							GUIState.getStage().show();
						}
						config.publishEvent(new ViewChangeEvent("EditView"));
					});
				}
			}

		});

		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if(flg){
					setLocation(e.getXOnScreen()-prefX, e.getYOnScreen()-prefY);
				}
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
			}
		});
		setAlwaysOnTop(true);
	}

	@PostConstruct
	private void initUI() throws Exception{
		ImageIcon icon;
		if (config.getTheme().equals("default")){
			icon = new ImageIcon(ImageIO.read(new ClassPathResource("style/ico-s.png").getInputStream()));
		}else {
			icon = new ImageIcon(ImageIO.read(new File("./configs/theme/"+config.getTheme()+"/icon.png")));
		}
		JLabel lblIco = new JLabel("");
		lblIco.setIcon(icon);
		add(lblIco);
	}

}
