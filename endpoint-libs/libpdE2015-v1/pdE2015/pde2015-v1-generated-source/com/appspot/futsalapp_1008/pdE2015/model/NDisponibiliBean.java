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
 * on 2015-07-27 at 15:44:57 UTC 
 * Modify at your own risk.
 */

package com.appspot.futsalapp_1008.pdE2015.model;

/**
 * Model definition for NDisponibiliBean.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the pdE2015. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class NDisponibiliBean extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String httpCode;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer nDisponibili;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String report;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String result;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getHttpCode() {
    return httpCode;
  }

  /**
   * @param httpCode httpCode or {@code null} for none
   */
  public NDisponibiliBean setHttpCode(java.lang.String httpCode) {
    this.httpCode = httpCode;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getNDisponibili() {
    return nDisponibili;
  }

  /**
   * @param nDisponibili nDisponibili or {@code null} for none
   */
  public NDisponibiliBean setNDisponibili(java.lang.Integer nDisponibili) {
    this.nDisponibili = nDisponibili;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getReport() {
    return report;
  }

  /**
   * @param report report or {@code null} for none
   */
  public NDisponibiliBean setReport(java.lang.String report) {
    this.report = report;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getResult() {
    return result;
  }

  /**
   * @param result result or {@code null} for none
   */
  public NDisponibiliBean setResult(java.lang.String result) {
    this.result = result;
    return this;
  }

  @Override
  public NDisponibiliBean set(String fieldName, Object value) {
    return (NDisponibiliBean) super.set(fieldName, value);
  }

  @Override
  public NDisponibiliBean clone() {
    return (NDisponibiliBean) super.clone();
  }

}
