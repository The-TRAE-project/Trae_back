/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PasswordGenerator {
  private final List<Rule> rules;

  private PasswordGenerator() {
    throw new UnsupportedOperationException("Empty constructor is not supported.");
  }

  private PasswordGenerator(Builder builder) {
    this.rules = builder.rules;
  }

  public String generate(int length) {
    if (length <= 0) {
      return "";
    }

    // shuffle rules
    List<Rule> shuffledRules = new ArrayList<>(rules);
    Collections.shuffle(shuffledRules);

    // random instance, you can use `Random random = new Random();`
    Random random = new SecureRandom();

    // 1. STAGE - SELECT MINIMUM CHARACTERS FROM RULES THAT HAVE MINIMUM COUNT.
    List<Character> selectedChars =
        new ArrayList<>(selectCharactersByMinimumCount(random, shuffledRules));

    // 2. STAGE - SELECT MISSING LENGTH FROM ALL AVAILABLE CHARACTERS
    int missingLength = length - selectedChars.size();
    if (missingLength > 0) {
      selectedChars.addAll(selectCharactersByMissingLength(random, shuffledRules, missingLength));
    }

    // 3. STAGE - SHUFFLE SELECTED CHARS
    Collections.shuffle(selectedChars);

    // 4. STAGE - RETURN RESULT
    return selectedChars.stream().map(String::valueOf).collect(Collectors.joining());
  }

  /**
   * Select random characters from filter rules that they are defined minimum count value.
   *
   * @param random Random instance
   * @param rules  Rules
   * @return Randomly chosen characters
   */
  private List<Character> selectCharactersByMinimumCount(Random random, List<Rule> rules) {
    return rules.stream()
        .filter(rule -> rule.minimumCount > 0)
        .flatMap(rule ->
            IntStream.range(0, rule.minimumCount)
                .mapToObj(i -> rule.text.charAt(random.nextInt(rule.text.length()))))
        .collect(Collectors.toList());
  }

  /**
   * Select random characters from all filter rules up to complete required characters count.
   *
   * @param random Random instance
   * @param rules  Rules
   * @return Randomly chosen characters
   */
  private List<Character> selectCharactersByMissingLength(
      Random random, List<Rule> rules, int missingLength) {
    List<Character> availableList = rules.stream()
        .flatMap(rule -> rule.text.chars().mapToObj(c -> (char) c))
        .collect(Collectors.toList());
    // shuffle available list
    Collections.shuffle(availableList);

    return IntStream.range(0, missingLength)
        .mapToObj(i -> availableList.get(random.nextInt(availableList.size())))
        .collect(Collectors.toList());
  }

  public static class Builder {
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String PUNCTUATION = "!@#$%&*+-";

    private final List<Rule> rules = new ArrayList<>();

    public Builder digits() {
      return custom(DIGITS, 0);
    }

    public Builder digits(int minimumCount) {
      return custom(DIGITS, minimumCount);
    }

    public Builder lower() {
      return lower(0);
    }

    public Builder lower(int minimumCount) {
      return custom(LOWER, minimumCount);
    }

    public Builder upper() {
      return upper(0);
    }

    public Builder upper(int minimumCount) {
      return custom(UPPER, minimumCount);
    }

    public Builder punctuation() {
      return punctuation(0);
    }

    public Builder punctuation(int minimumCount) {
      return custom(PUNCTUATION, minimumCount);
    }

    public Builder custom(String text) {
      return custom(text, 0);
    }

    public Builder custom(String text, int minimumCount) {
      rules.add(new Rule(text, minimumCount));
      return this;
    }

    public PasswordGenerator build() {
      return new PasswordGenerator(this);
    }

    public String generate(int length) {
      return build().generate(length);
    }
  }

  private static class Rule {
    private final String text;
    private final int minimumCount;

    public Rule(String text, int minimumCount) {
      this.text = text;
      this.minimumCount = minimumCount;
    }
  }
}