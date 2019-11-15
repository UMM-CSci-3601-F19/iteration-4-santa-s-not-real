package umm3601.notification;

public class SmtpProperties {

  public static final int DEFAULT_SMTP_PORT = 25;

  private String host;
  private int port;
  private boolean useAuthentication;
  private String userName;
  private String password;
  private boolean useSSL;
  private String defaultSender;

  public SmtpProperties(final String host, final int port, final boolean useAuthentication,
                        final String userName, final String password, final boolean useSSL, final String defaultSender){
    this(host, String.valueOf(port), useAuthentication, userName, password, useSSL, defaultSender);
  }

  public SmtpProperties(final String host, final String port, final boolean useAuthentication,
                        final String userName, final String password, final boolean useSSL, final String defaultSender){
    this.host = host;
    this.port = port == null ? DEFAULT_SMTP_PORT : Integer.parseInt(port);
    this.useAuthentication = useAuthentication;
    this.userName = userName;
    this.password = password;
    this.useSSL = useSSL;
    this.defaultSender = defaultSender;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public boolean isUseAuthentication() {
    return useAuthentication;
  }

  public String getUserName() {
    return userName;
  }

  public String getPassword() {
    return password;
  }

  public String getFrom() {
    return defaultSender;
  }

  public boolean isUseSSL() {
    return useSSL;
  }

  public void setHost(final String host) {
    this.host = host;
  }

  public void setPort(final int port) {
    this.port = port;
  }

  public void setUseAuthentication(final boolean useAuthentication) {
    this.useAuthentication = useAuthentication;
  }

  public void setUserName(final String userName) {
    this.userName = userName;
  }

  public void setPassword(final String password) {
    this.password = password;
  }

  public void setUseSSL(final boolean useSSL) {
    this.useSSL = useSSL;
  }

  public void setDefaultSender(final String defaultSender) {
    this.defaultSender = defaultSender;
  }
}
