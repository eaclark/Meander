/*
 * Copyright 2011 Ed Clark
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package meander

/**
 * enumerations of the different page sizes supported
 */
enum PageType {
    Default( null, null, null, null),
    LETTER( 8.5f, 11.0f, 'in', 72i),
    LEDGER( 11.0f, 17.0f, 'in', 72i),
    A3( 297, 420, 'mm', 2.834646f),
    A4( 210, 297, 'mm', 2.834646f),
    B4( 250, 353, 'mm', 2.834646f),
    B5( 176, 250, 'mm', 2.834646f)

    private final x  // short side
    private final y  // long side
    private final d  // dimensional unit
    private final p  // points per unit
    private static dfltX
    private static dfltY
    private static dfltD
    private static dfltP

    PageType( x, y, d, p) {
        this.x = x
        this.y = y
        this.d = d
        this.p = p
    }

    public static setDefaults( x, y, d, p) {
        dfltX = x
        dfltY = y
        dfltD = d
        dfltP = p
    }

    public x() { return x ?: dfltX }
    public y() { return y ?: dfltY }
    public d() { return d ?: dfltD }
    public p() { return p ?: dfltP }
}