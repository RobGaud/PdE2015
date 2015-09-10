/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2015-07-16 18:28:29 UTC)
 * on 2015-08-02 at 10:02:40 UTC 
 * Modify at your own risk.
 */

package com.appspot.futsalapp_1008.pdE2015.model;

/**
 * Model definition for ListaInvitiBean.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the pdE2015. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class ListaInvitiBean extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String httpCode;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<InfoInvitoBean> listaInviti;

  static {
    // hack to force ProGuard to consider InfoInvitoBean used, since otherwise it would be stripped out
    // see http://code.google.com/p/google-api-java-client/issues/detail?id=528
    com.google.api.client.util.Data.nullOf(InfoInvitoBean.class);
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getHttpCode() {
    return httpCode;
  }

  /**
   * @param httpCode httpCode or {@code null} for none
   */
  public ListaInvitiBean setHttpCode(java.lang.String httpCode) {
    this.httpCode = httpCode;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<InfoInvitoBean> getListaInviti() {
    return listaInviti;
  }

  /**
   * @param listaInviti listaInviti or {@code null} for none
   */
  public ListaInvitiBean setListaInviti(java.util.List<InfoInvitoBean> listaInviti) {
    this.listaInviti = listaInviti;
    return this;
  }

  @Override
  public ListaInvitiBean set(String fieldName, Object value) {
    return (ListaInvitiBean) super.set(fieldName, value);
  }

  @Override
  public ListaInvitiBean clone() {
    return (ListaInvitiBean) super.clone();
  }

}
