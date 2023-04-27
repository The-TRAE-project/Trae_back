/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.dto.operation;

/**
 * This class represents the operation information for a project template.
 *
 * @param name              The name of the operation.
 * @param isEnded           Whether the operation has been completed or not.
 * @param inWork            Whether the operation is currently in progress or not.
 * @param readyToAcceptance Whether the operation is ready for acceptance or not.
 */
public record OperationInfoForProjectTemplateDto(
    String name,
    boolean isEnded,
    boolean inWork,
    boolean readyToAcceptance) {
}
