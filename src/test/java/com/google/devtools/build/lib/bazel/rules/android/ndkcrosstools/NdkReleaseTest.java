// Copyright 2015 The Bazel Authors. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.devtools.build.lib.bazel.rules.android.ndkcrosstools;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Test for NdkRelease.
 */
@RunWith(JUnit4.class)
public class NdkReleaseTest {

  @Test
  public void testReleaseParsing() {
    testNdkRelease("r8",                "8",  null, null, false);
    testNdkRelease("r8 (64-bit)",       "8",  null, null, true);
    testNdkRelease("r10",               "10", null, null, false);
    testNdkRelease("r10 (64-bit)",      "10", null, null, true);
    testNdkRelease("r10-rc4",           "10", null, "rc4",  false);
    testNdkRelease("r10-rc4 (64-bit)",  "10", null, "rc4",  true);
    testNdkRelease("r10e",              "10", "e",  null,  false);
    testNdkRelease("r10e (64-bit)",     "10", "e",  null,  true);
    testNdkRelease("r10e-rc4",          "10", "e",  "rc4", false);
    testNdkRelease("r10e-rc4 (64-bit)", "10", "e",  "rc4", true);

    try {
      // this is actually invalid
      testNdkRelease("r10e-rc4 (abc)", "10", "e", "rc4", false);
      throw new Error();
    } catch (AssertionError e) {
      // expected
    }
  }
  
  private static void testNdkRelease(
      String ndkReleaseString,
      String majorRelease,
      String minorRelease,
      String releaseCandidate,
      boolean is64Bit) {

    NdkRelease ndkRelease = NdkRelease.create(ndkReleaseString);
    assertThat(ndkRelease.isValid).isTrue();
    assertThat(ndkRelease.rawRelease).isEqualTo(ndkReleaseString);
    assertThat(majorRelease).isEqualTo(ndkRelease.majorRevision);
    assertThat(minorRelease).isEqualTo(ndkRelease.minorRevision);
    assertThat(releaseCandidate).isEqualTo(ndkRelease.releaseCandidate);
    assertThat(is64Bit).isEqualTo(ndkRelease.is64Bit);
  }

  @Test
  public void testBadRelease() {
    testBadNdkRelease("");
    testBadNdkRelease("r");
    testBadNdkRelease("rZ");
    testBadNdkRelease("r10erc4");
    testBadNdkRelease("r10e-rcZ");
    testBadNdkRelease("r10e-rc4 64-bit");
    testBadNdkRelease("r10e-rc4 abc");
    testBadNdkRelease("r10e-rc4 (64-bit) abc");
    testBadNdkRelease("r10e-rc4 (abc)");

    try {
      // this is actually valid
      testBadNdkRelease("r10e-rc4 (64-bit)");
      throw new Error();
    } catch (AssertionError e) {
      // expected
    }
  }

  private static void testBadNdkRelease(String ndkReleaseString) {
    NdkRelease ndkRelease = NdkRelease.create(ndkReleaseString);
    assertThat(ndkRelease.isValid).isFalse();
    assertThat(ndkRelease.rawRelease).isEqualTo(ndkReleaseString);
    assertThat(ndkRelease.majorRevision).isNull();
    assertThat(ndkRelease.minorRevision).isNull();
    assertThat(ndkRelease.releaseCandidate).isNull();
    assertThat(ndkRelease.is64Bit).isFalse();
  }
}
