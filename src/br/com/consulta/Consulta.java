package br.com.consulta;
import java.sql.*;
import java.sql.DriverManager;
import java.util.Vector;
import java.util.Calendar;  
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import java.awt.Dimension;  
import java.text.DateFormat;  
import java.text.SimpleDateFormat;

/**
 * @author Isac Canedo
 * Date: 02/2011
 */
  

class Consulta extends JFrame implements ActionListener
{
  JLabel L1,L2;
  JTextField T1,T2;     
  JButton B1;
  ResultSet rs;
  Statement MeuState;

  static public TimerTest fTimerTest;
  
  public class TimerTest extends JPanel {  
	  
	    JLabel label;  	  
	    JButton startButton;  
	    JButton stopButton;	      
	    DateFormat dateFormat;	      
	    Calendar calendar;  	      
	    Timer timer;  
	 

	 public TimerTest() {  
	        this.dateFormat = new SimpleDateFormat("HH:mm:ss");  
	        this.calendar = Calendar.getInstance();  
	        this.calendar.set(Calendar.MILLISECOND, 0);  
	        this.calendar.set(Calendar.SECOND, 0);  
	        this.calendar.set(Calendar.MINUTE, 0);  
	        this.calendar.set(Calendar.HOUR_OF_DAY, 0);          
	          
	        this.initialize();
	    }  
	  
	    protected void initialize() {  
	        this.add(this.getLabel());  
	        this.add(this.getStartButton());  
	        this.add(this.getStopButton());  
	        this.go();  
	    }  
	  
	    public JLabel getLabel() {  
	        if (this.label == null) {  
	            this.label = new JLabel(getTime());  
	            this.label.setPreferredSize(new Dimension(100, 22));  
	        }  
	        return this.label;  
	    }  
	      
	    public JButton getStartButton() {  
	        if (this.startButton == null) {  
	            this.startButton = new JButton("Start");  
	            this.startButton.setPreferredSize(new Dimension(75, 22));  
	            this.startButton.addActionListener(new ActionListener() {  
	                public void actionPerformed(ActionEvent e) {                      
	                    if (!timer.isRunning()) {  
	                        timer.start();  
	                    }  
	                }  
	            });  
	        }  
	        return this.startButton;  
	    }  
	      
	    public JButton getStopButton() {  
	        if (this.stopButton == null) {  
	            this.stopButton = new JButton("Stop");  
	            this.stopButton.setPreferredSize(new Dimension(75, 22));  
	            this.stopButton.addActionListener(new ActionListener() {  
	                public void actionPerformed(ActionEvent e) {                      
	                    if (timer.isRunning()) {  
	                        timer.stop();  
	                    }  
	                }  
	            });              
	        }  
	          
	        return this.stopButton;  
	    }  
	    
	   public void Stop() {		   
		   if (timer.isRunning()) {  
               timer.stop();  
           }  		   
	   }	    
	   
	  
	   public void showinfo() {
		   JOptionPane.showMessageDialog(null,"Erro ao abrir o arquivo !");
	   }
	    
	    
	    public void go() {  
	        ActionListener action = new ActionListener() {  
	            public void actionPerformed(ActionEvent e) {  
	                TimerTest.this.label.setText(getTime());  
	            }  
	        };  
	        this.timer = new Timer(1000, action);  
	        this.timer.start();  
	    }  
	  
	    public String getTime() {  
	        this.calendar.add(Calendar.MILLISECOND, 1000);  
	        return this.dateFormat.format(this.calendar.getTimeInMillis());  
	    }  	   
	}    

  private JanelaPesquisaSQL novajanela;  
 
  
  public class JanelaPesquisaSQL extends JDialog{
      Connection conexao;
      JTable tab;

      public JanelaPesquisaSQL(Frame owner, String title, boolean modal){ 
         super(owner, title, modal);
         setSize(600, 600);
         setLocationRelativeTo(null);
         JFrame frame = new JFrame();  
         frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
         fTimerTest = new TimerTest(); 
         frame.setContentPane(fTimerTest);  
         frame.setSize(350, 75);  
         frame.setLocationRelativeTo(null);
         frame.setVisible(true);    
         frame.setTitle("Contador do Tempo para executar SQL");
         frame.setResizable(false); 
         
       }
       
       public boolean buscaPesquisa()
       {
          Statement st;
          ResultSet res; 
         
          try          
           {
              String url = "jdbc:sqlserver://UIPABD32P\\DESV:1433;databaseName=DB_ATIVOS_ORCAMENTO";
              try
                {
                Class.forName ( "com.microsoft.sqlserver.jdbc.SQLServerDriver" );
                conexao = DriverManager.getConnection (url, "serv_ativos_01", "Ativos_01" );
                }
              catch (ClassNotFoundException cne)
                {
                 System.out.println("Erro ao carregar o driver JDBC/ODBC");
                 return false;
                }
              catch (SQLException sqlne )
                {
                System.out.println("Problemas na Conexão");
                return false;
                }      
              
           Vector cabecalho = new Vector();
           Vector linhas = new Vector(); 
           st = conexao.createStatement();
           String SQL = T1.getText();

           T2.setText("Consulta gerada com Sucesso");
   
           res = st.executeQuery (SQL);
           res.next();
           // busca os cabeçalhos
           ResultSetMetaData rsmd = res.getMetaData();
           for ( int i = 1; i <= rsmd.getColumnCount(); ++i )
              cabecalho.addElement( rsmd.getColumnName ( i ) );
           // busca dados das linhas
           do
            {
            linhas.addElement( proximaLinha( res, rsmd ) );
            }
           while ( res.next() );

           // Mostra a tabela com cabeçalhos e registros 
           tab = new JTable( linhas, cabecalho );
           JScrollPane scroller = new JScrollPane( tab );
           getContentPane().add(scroller, BorderLayout.CENTER);
           validate();
           st.close();
           } 
          catch (SQLException sqlex)
            { 
        	  JOptionPane.showMessageDialog(null,"Erro na Consulta SQL");
        	  T2.setText("Consulta Inválida");
        	  return false;
            }
          return true;
       }

       private Vector proximaLinha(ResultSet rs, ResultSetMetaData rsmd )
       {
        Vector LinhaAtual = new Vector();
        try
        {
        for ( int i = 1; i <= rsmd.getColumnCount(); ++i )
         switch( rsmd.getColumnType(i))
         {
         case Types.VARCHAR:
          LinhaAtual.addElement(rs.getString(i));break;
         case Types.CHAR:
             LinhaAtual.addElement(rs.getString(i));break;   
         case Types.TIMESTAMP:
          LinhaAtual.addElement (rs.getDate(i));break;
         case Types.INTEGER :
             LinhaAtual.addElement (rs.getInt(i));break; 
         case Types.NUMERIC :
             LinhaAtual.addElement (rs.getString(i));break;                
         }
        }
        catch(SQLException e) {}
        return LinhaAtual;
       }   
       
  } 
     
  public static void main(String args[])
  {
   JFrame Janela = new Consulta();
   Janela.setVisible(true);
   WindowListener x = new WindowAdapter()
    {
    public void windowClosing(WindowEvent e)
     {
      System.exit(0);
     }
    };
   Janela.addWindowListener(x);
  }
  
  Consulta()
  {   
   setTitle("Módulo SQL");
   setSize(600,170);
   setLocationRelativeTo(null);
   setResizable(false); 
   getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT));
   L1 = new JLabel("Digite o comando SQL:");
   L2 = new JLabel("Resultado do comando:");
   T1 = new JTextField(50);
   T1.setText("SELECT * FROM FILMES");
   
   T1.addActionListener(this);
   T2 = new JTextField(50);
   T2.setEnabled(false); //apenas como leitura (read-only)
   B1 = new JButton("Consultar");
   //B1.addActionListener(this);
   B1.addActionListener (
           new ActionListener(){
             public void actionPerformed(ActionEvent e){
                       	 
            novajanela = new JanelaPesquisaSQL(null, "Resultado da Pesquisa", true);
            novajanela.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE );
           if (novajanela.buscaPesquisa()) {
            novajanela.setVisible (true);
           }
           else
           {
        	fTimerTest.Stop();
        	fTimerTest.setVisible(false);    
           }
           
            fTimerTest.Stop();
             
             }
           }
         );
  
   getContentPane().add(L1);
   getContentPane().add(T1);
   getContentPane().add(L2); 
   getContentPane().add(T2);
   getContentPane().add(B1);
   
  }    

  public void actionPerformed(ActionEvent e)
   {  
    if (e.getSource()==B1)
      { 
    	
      }
   }    
}




