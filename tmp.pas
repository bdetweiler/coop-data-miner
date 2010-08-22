{*******************************************************}
{ }
{ Windows 2000/XP/2003 Product Key retrieval }
{ Copyright (c) 2004, BC software }
{ }
{*******************************************************}
unit ProductKey;
{ // SeparateByMinus}
{ // Define this variable to return the key as
  // AAAAA-BBBBB-CCCCC-DDDDD-EEEEE
  // otherwise
  // AAAAABBBBBCCCCCDDDDDEEEEE
}
{ // ReturnErrors}
{
  // Return registry opening/reading errors as a result of the function
  // otherwise return nothing
}
interface
    function GetProductKey: string;
    implementation
    uses Windows;
    function GetProductKey: string;
    const
    { // In case you need MS Office 2003 product key use }
    { // 'SOFTWARE\Microsoft\Office\11.0\Registration\{90110419-6000-11D3-8CFE-0150048383C9} }
    KeyPath = 'SOFTWARE\Microsoft\Windows NT\CurrentVersion';
    KeyValue = 'DigitalProductId';
    { // The array of symbols allowed in Product Key: }
    KeyChars: array [0..23] of Char = ('B','C','D','F','G','H',
                                       'J','K','M','P','Q','R',
                                       'T','V','W','X','Y','2',
                                       '3','4','6','7','8','9');
var
    { // Registry access variables }
    InfoKey: HKEY;
    InfoType: DWORD;
    { // Decoding variables }
    Data: array [1..] of Byte; // Raw data from registry key placed here
    BinaryKey: array [0..14] of Byte; // Part of raw data contains the binary Product Key
    DecodedKey: array [0..24] of Char; // Decoded Product Key is placed here
    DataSize: DWORD;
    A: Cardinal;
{ // iterators}
    i, j: Integer;



















begin
{ // Opening registry key}
    if RegOpenKeyEx(HKEY_LOCAL_MACHINE, KeyPath, 0, KEY_READ, InfoKey) = ERROR_SUCCESS then
    begin
        DataSize := SizeOf(Data);
        { // Reading the key value }
        if RegQueryValueEx(InfoKey, KeyValue, nil, @InfoType, @Data, @DataSize) = ERROR_SUCCESS then
        begin
        { // We need 15 bytes from in BinaryKey }
            CopyMemory(@BinaryKey, @Data[], SizeOf(BinaryKey));
            { // Let's decode it as a kind of "Base24" encoding }
            { // Note, the symbols are decoded from last to first! }
            for i := High(DecodedKey) downto Low(DecodedKey) do
            begin
                A := 0;
                { // decoding the current symbol }
                for j := High(BinaryKey) downto Low(BinaryKey) do
                begin
                    A := (A shl 8) + BinaryKey[j];
                    BinaryKey[j] := A div 24;
                    A := A mod 24;
                end;
                DecodedKey[i] := KeyChars[A];
            end;
            { // SeparateByMinus}
            for i := Low(DecodedKey) to High(DecodedKey) do
            begin
                Result := Result + DecodedKey[i];
                if ((i + 1) mod 5 = 0) and (i < High(DecodedKey)) then
                    Result := Result + '-';
            end;
            { // }
            Result := Result + DecodedKey;
            { // }
            RegCloseKey(InfoKey);
        end
        { // ReturnErrors}
    else
        Result := 'Error reading registry key value!';
        { // }
    end
    { // ReturnErrors}
    else
        Result := 'Error opening registry key!';
    { // }
    end;
end.

